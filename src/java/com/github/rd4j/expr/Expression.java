package com.github.rd4j.expr;


public interface Expression {
	Expression getNext();
	TypedReference eval(Object root);
}
