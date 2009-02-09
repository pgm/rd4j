package com.github.rd4j.djangoishtemplate;

public class LiteralExpression implements ParsedExpression {
	final Object value;
	
	public LiteralExpression(Object value)
	{
		this.value = value;
	}
	
	public Object get(Object context) {
		return this.value;
	}

	public void set(Object context, Object value) {
		throw new UnsupportedOperationException();
	}

}
