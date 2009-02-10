package com.github.rd4j.djangoishtemplate;

public interface ExpressionParser {
	ParsedExpression parseExpression(String expression);
	ExpressionContext createContext();
}
