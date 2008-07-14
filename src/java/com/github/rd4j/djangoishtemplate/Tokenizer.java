package com.github.rd4j.djangoishtemplate;

import java.io.Reader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.rd4j.expr.BaseTokenizer;

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
		return new Token(type, identity.toString());
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
	
	BlockPattern patterns [] = new BlockPattern[] {
			new BlockPattern( ForToken.class, Pattern.compile("^\\s*for\\s+(\\S+)\\s+in\\s+(\\S+)\\s*$") ),
			new BlockPattern( IfToken.class, Pattern.compile("^\\s*if\\s+(\\S+)\\s*$") ),
			new BlockPattern( BlockToken.class, Pattern.compile("^\\s*block\\s+(\\S+)\\s*$") ),
			new BlockPattern( EndBlockToken.class, Pattern.compile("^\\s*endblock\\s+(\\S+)\\s*$") ),
			new BlockPattern( ExtendsToken.class, Pattern.compile("^\\s*extends\\s+(\\S+)\\s*$") )
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
		}

		for(BlockPattern bp : patterns) {
			Matcher m = bp.pattern.matcher(image);
			if(m.matches()) {
				String args[] = new String[m.groupCount()+1];
				Class<?> types[] = new Class<?>[args.length];
				
				args[0] = image;
				for(int i=0;i<m.groupCount();i++) {
					args[i+1] = m.group(i+1);
				}

				for(int i=0;i<types.length;i++) {
					types[i] = String.class;
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

		public ExtendsToken(String image, String name ) {
			super(TokenType.EXTENDS, image);
			this.name = name;
		}
	}
	
	public static class ForToken extends Token {
		final String elmExpr;
		final String colExpr;

		public ForToken(String image, String elmExpr, String colExpr ) {
			super(TokenType.FOR, image);
			this.elmExpr = elmExpr;
			this.colExpr = colExpr;
		}
	}
	
	public static class IfToken extends Token {
		final String expr;

		public IfToken(String image, String expr ) {
			super(TokenType.IF, image);
			this.expr = expr;
		}
	}
	
	public static class BlockToken extends Token {
		final String name;

		public BlockToken(String image, String name ) {
			super(TokenType.BLOCK, image);
			this.name = name;
		}
	}

	public static class EndBlockToken extends Token {
		final String name;

		public EndBlockToken(String image, String name ) {
			super(TokenType.ENDBLOCK, image);
			this.name = name;
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
