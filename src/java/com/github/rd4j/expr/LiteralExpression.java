package com.github.rd4j.expr;

import java.lang.reflect.Type;

public class LiteralExpression implements Expression{
	final Object value;
	
	public LiteralExpression(Object value) {
		super();
		this.value = value;
	}

	class LiteralTypedReference implements TypedReference {

		public Object get() {
			return value;
		}

		public Type getType() {
			return value.getClass();
		}

		public void set(Object obj) {
			throw new RuntimeException("Cannot set the value of a literal");
		}
	}
	
	LiteralTypedReference typedReference = new LiteralTypedReference();
	
	public TypedReference eval(Object root) {
		return typedReference;
	}

	public Expression getNext() {
		return null;
	}
	
}
