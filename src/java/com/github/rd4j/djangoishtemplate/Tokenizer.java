package com.github.rd4j.djangoishtemplate;

import java.io.Reader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.rd4j.expr.BaseTokenizer;
import com.github.rd4j.expr.Expression;
import com.github.rd4j.expr.LiteralExpression;

public class Tokenizer extends BaseTokenizer {
	List<Token> tokenLookahead = new ArrayList<Token>();
	
	public Tokenizer(Reader reader) {
		super(reader);
	}

	protected void readUntil(char c1, char c2, boolean errorOnEof)
	{	
		// read until we see a }}
		while(true) {
			int c = getc();
			if(c < 0) {
				if(errorOnEof)
					throw new RuntimeException("unexpected EOF");
				
				return;
			}
			
			if(c == c1) {
				int lookahead = getc();
				if(lookahead == c2) {
					return;
				}
				// only one '}'
				ungetc(lookahead);
			}
			
			identity.append((char)c);
		}
	}
	
	public Token simpleToken(TokenType type) {
		Token t = new Token(type, identity.toString(), curTokenStartLine, 
				curTokenStartColumn, lastReadLine, lastReadColumn);

		aboutToReturnToken();
	
		return t;
	}

	public void pushNextToken(Token t) {
		tokenLookahead.add(t);
	}

	static class BlockPattern {
		final Pattern pattern;
		final Class<? extends Token>tkClass;
		
		public BlockPattern(Class<? extends Token> tkClass, Pattern pattern) {
			super();
			this.pattern = pattern;
			this.tkClass = tkClass;
		}
	}
	
	// macro syntax:
	//	macroName param1="strvalue" param2="${obj} begin"
	//    ...body...
	//  end
	//
	//  or
	//
	//  {{ macroName param1="strvalue" param2="${obj}" }}
	//
	BlockPattern patterns [] = new BlockPattern[] {
			new BlockPattern( ForToken.class, Pattern.compile("^\\s*for\\s+(\\S+)\\s+in\\s+(\\S+)\\s*$") ),
			new BlockPattern( IfToken.class, Pattern.compile("^\\s*if\\s+(\\S+)\\s*$") ),
			new BlockPattern( BlockToken.class, Pattern.compile("^\\s*block\\s+(\\S+)\\s*$") ),
			new BlockPattern( EndBlockToken.class, Pattern.compile("^\\s*endblock\\s+(\\S+)\\s*$") ),
			new BlockPattern( ExtendsToken.class, Pattern.compile("^\\s*extends\\s+(\\S+)\\s*$") ),
			new BlockPattern( MacroToken.class, Pattern.compile("^\\s*(\\S+)\\s+((?:\\S+=\"[^\"]*\"\\s*)*)((?:begin)?)\\s*$") )
	};
	
	public Token blockToken() {
		String image = identity.toString();
		
		image = image.trim();
		
		if(image.equals("endif")) {
			return simpleToken(TokenType.ENDIF);
		} else if(image.equals("endfor")) {
			return simpleToken(TokenType.ENDFOR);
		} else if(image.equals("else")) {
			return simpleToken(TokenType.ELSE);
		} else if(image.equals("endblock")) {
			return simpleToken(TokenType.ENDBLOCK);
		} 

		for(BlockPattern bp : patterns) {
			Matcher m = bp.pattern.matcher(image);
			if(m.matches()) {
				Object[] args = new Object[m.groupCount()+1+4];
				Class<?> types[] = new Class<?>[args.length];
				
				args[0] = image;
				for(int i=0;i<m.groupCount();i++) {
					args[i+1] = m.group(i+1);
				}
				int argIndex = m.groupCount();
				args[argIndex+1] = new Integer(this.curTokenStartColumn); 
				args[argIndex+2] = new Integer(this.curTokenStartLine); 
				args[argIndex+3] = new Integer(this.lastReadColumn); 
				args[argIndex+4] = new Integer(this.lastReadLine); 

				for(int i=0;i<types.length;i++) {
					if(i < m.groupCount()+1)
						types[i] = String.class;
					else
						types[i] = Integer.TYPE;
				}
				
				Object instance;
				try {
					Constructor<?> constructor;
					constructor = bp.tkClass.getConstructor(types);
					instance = constructor.newInstance((Object[])args);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				
				return (Token)instance;
			}
		}
		
		throw new RuntimeException("unrecognized block: "+image);
	}
	
	public static class ExtendsToken extends Token {
		final String name;

		public ExtendsToken(String image, String name, int startLine, int stopLine, int startColumn, int stopColumn ) {
			super(TokenType.EXTENDS, image, startLine, stopLine, startColumn, stopColumn);
			this.name = name;
		}
	}
	
	public static class ForToken extends Token {
		final String elmExpr;
		final String colExpr;

		public ForToken(String image, String elmExpr, String colExpr, int startLine, int stopLine, int startColumn, int stopColumn ) {
			super(TokenType.FOR, image, startLine, stopLine, startColumn, stopColumn);
			this.elmExpr = elmExpr;
			this.colExpr = colExpr;
		}
	}
	
	public static class IfToken extends Token {
		final String expr;

		public IfToken(String image, String expr, int startLine, int stopLine, int startColumn, int stopColumn ) {
			super(TokenType.IF, image, startLine, stopLine, startColumn, stopColumn);
			this.expr = expr;
		}
	}
	
	public static class BlockToken extends Token {
		final String name;

		public BlockToken(String image, String name, int startLine, int stopLine, int startColumn, int stopColumn ) {
			super(TokenType.BLOCK, image, startLine, stopLine, startColumn, stopColumn);
			this.name = name;
		}
	}

	public static class EndBlockToken extends Token {
		final String name;

		public EndBlockToken(String image, String name, int startLine, int stopLine, int startColumn, int stopColumn ) {
			super(TokenType.ENDBLOCK, image, startLine, stopLine, startColumn, stopColumn);
			this.name = name;
		}
	}
	
	public static class MacroToken extends Token {
		final boolean hasBodyFlag;
		final String macroName;
		final Map<String, Expression> args;
		
		public MacroToken(String image, String macroName, String args, String beginStr, int startLine, int stopLine, int startColumn, int stopColumn) {
			super(TokenType.MACRO, image, startLine, stopLine, startColumn, stopColumn);
			this.hasBodyFlag = beginStr.equals("begin");
			this.macroName = macroName;
			this.args = parseArgs(args);
		}
		
		static final Pattern argNameAndValue = Pattern.compile("^\\s*(\\S+)\\s*=\\s*\"([^\"]*)\"");
		
		public static Map<String, Expression> parseArgs(String args) {
			Matcher m = argNameAndValue.matcher(args);
			int nextStart = 0;
			Map<String, Expression> argMap = new HashMap<String, Expression>();

			while(true) {
				m.region(nextStart, args.length());
				if(!m.find()) {
					break;
				}
				
				String varName = m.group(1);
				String value = m.group(2);
				
				nextStart = m.end();
				
				// TODO: check for ${} syntax
				//Expression expr = ExpressionUtil.parseExpression(value);
				Expression expr = new LiteralExpression(value);
				argMap.put(varName, expr);
			}
			
			// verify there is nothing left
			String remaining = args.substring(nextStart).trim();
			if(remaining.length() > 0) {
				throw new RuntimeException("Could not parse argument: "+remaining);
			}
			
			return argMap;
		}
	}
	
	public Token getNextToken() {
		if(tokenLookahead.size() > 0) {
			return tokenLookahead.remove(tokenLookahead.size()-1);
		}
		
		identity.setLength(0);

		int c = getc();
		if(c < 0) {
			return simpleToken(TokenType.EOF);
		}
		
		if(c == '{') {
			int lookahead = c = getc();
			if(lookahead == '%') {
				readUntil('%', '}', false);
				return blockToken();
			} else if(lookahead == '{') {
				readUntil('}', '}', false);
				return simpleToken(TokenType.VARIABLE);
			} else if(lookahead == '#') {
				readUntil('#', '}', false);
				return simpleToken(TokenType.COMMENT);
			} else {
				ungetc(lookahead);
				ungetc(c);
			}
		}

		// probably refactor this...
		while(true) {
			if(c < 0) {
				return simpleToken(TokenType.RAWTEXT);
			}
				
			if(c == '{') {
				int lookahead = getc();
				if(lookahead == '{' || lookahead == '%' || lookahead == '#') {
					ungetc(lookahead);
					ungetc(c);
					return simpleToken(TokenType.RAWTEXT);
				}
				// only one '{'
				ungetc(lookahead);
			}
				
			identity.append((char)c);
			c = getc();
		}
	}
}
