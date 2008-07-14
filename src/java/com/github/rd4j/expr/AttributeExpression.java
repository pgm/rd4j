package com.github.rd4j.expr;


public class AttributeExpression implements Expression {
	final String attribute;
	final Expression next;
	
	public AttributeExpression(String attribute, Expression next) {
		this.attribute = attribute;
		this.next = next;
	}

	public Expression getNext() {
		return this.next;
	}
	
	public TypedReference eval(Object root) {
		TypedReference reference = ExpressionUtil.getReference(root, attribute);
		return reference;
	}
	
	public String toString() {
		return "."+attribute;
	}
	
	public String getAttribute() {
		return attribute;
	}
}
