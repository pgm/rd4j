package com.github.rd4j.expr;

import com.github.rd4j.form.types.Rd4jType;

public interface TypedReference {
	public Object get();
	public Rd4jType getType(); 
	public void set(Object obj);
}
