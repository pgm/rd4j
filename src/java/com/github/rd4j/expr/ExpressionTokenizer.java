package com.github.rd4j.expr;

import java.io.StringReader;


class ExpressionTokenizer extends BaseTokenizer {
	static final int EOF = 1;
	static final int DOT = 2;
	static final int IDENTIFIER = 3;
	static final int STRING = 4;
	static final int NUMBER = 5;

	
	public ExpressionTokenizer(String exprStr) {
		super(new StringReader(exprStr));
	}
	
	public int getNextToken() {
		identity.setLength(0);
		int c = getc();

		while(Character.isWhitespace(c)) {
			c = getc();
		}
		
		if(c < 0) {
			return EOF;
		}
		
		identity.append((char)c);
		
		if(c=='[') {
			return '[';
		}
		
		if(c==']') {
			return ']';
		}
		
		if(c=='.') {
			return '.';
		}
		
		if(c=='\'') {
			while(true) {
				c = getc();
				if(c < 0)
					break;
				identity.append((char)c);
				if(c == '\'') {
					break;
				}
			}
			
			return STRING;
		}
		
		if(c=='\"') {
			while(true) {
				c = getc();
				if(c < 0)
					break;
				identity.append((char)c);
				if(c == '\"') {
					break;
				}
			}
			
			return STRING;
		}
		
		// otherwise assume we're an identifier
		while(true) {
			c = getc();
			if(c < 0)
				break;
			if(!Character.isJavaIdentifierPart(c)) {
				ungetc(c);
				break;
			}
			identity.append((char)c);
		}
		return IDENTIFIER;
	}
}
