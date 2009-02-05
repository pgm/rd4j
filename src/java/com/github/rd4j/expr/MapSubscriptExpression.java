package com.github.rd4j.expr;

import java.lang.reflect.Type;


public class MapSubscriptExpression implements Expression {
	final String subscript;
	final Expression next;
	final Type type;
	
	public MapSubscriptExpression(String subscript, Type type, Expression next) {
		this.subscript = subscript;
		this.next = next;
		this.type = type;
	}
	
	public TypedReference eval(Object root) {
		TypedReference reference = ExpressionUtil.getReferenceFromIndex(root, subscript);
		return reference;
	}

	public Expression getNext() {
		return next;
	}

}
