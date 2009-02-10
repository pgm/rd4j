package com.github.rd4j.djangoishtemplate;

public class LiteralExpression implements ParsedExpression {
	final Object value;
	
	public LiteralExpression(Object value)
	{
		this.value = value;
	}
	
	public Object get(ExpressionContext context) {
		return this.value;
	}

	public void set(ExpressionContext context, Object value) {
		throw new UnsupportedOperationException();
	}

}
