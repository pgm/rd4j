package com.github.rd4j.expr;

import com.github.rd4j.form.FormBinder;

public class SubscriptExpression implements Expression {
	final int subscript;
	final Expression next;
	
	public SubscriptExpression(int subscript, Expression next) {
		this.subscript = subscript;
		this.next = next;
	}
	
	public TypedReference eval(Object root) {
		TypedReference reference = ExpressionUtil.getReferenceFromIndex(root, subscript);
		return reference;
	}

	public Expression getNext() {
		return next;
	}

}
