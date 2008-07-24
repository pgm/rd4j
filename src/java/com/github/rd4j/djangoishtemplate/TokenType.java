package com.github.rd4j.djangoishtemplate;

public enum TokenType {
	RAWTEXT,
	BLOCK,
	IF,
	ENDIF,
	ENDBLOCK,
	FOR,
	ENDFOR,
	ELSE,
	VARIABLE,
	COMMENT,
	EXTENDS,
	MACRO,
	EOF;
}
