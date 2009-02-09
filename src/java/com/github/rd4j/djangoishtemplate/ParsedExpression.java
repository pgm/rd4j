package com.github.rd4j.djangoishtemplate;

public interface ParsedExpression {
	public Object get(Object context);
	public void set(Object context, Object value);
}
