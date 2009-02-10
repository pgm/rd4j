package com.github.rd4j.djangoishtemplate;

public interface ParsedExpression {
	public Object get(ExpressionContext context);
	public void set(ExpressionContext context, Object value);
}
