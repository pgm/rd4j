package com.github.rd4j.djangoishtemplate;

public class Token {
	final TokenType type;

	final String image;

	final int startLine;
	final int stopLine;
	final int startColumn;
	final int stopColumn;
	
	public Token(TokenType type, String image, int startLine, int stopLine, int startColumn, int stopColumn) {
		super();
		this.type = type;
		this.image = image;
		this.startLine = startLine;
		this.startColumn = startColumn;
		this.stopColumn  = stopColumn;
		this.stopLine = stopLine;
	}
	
	
}
