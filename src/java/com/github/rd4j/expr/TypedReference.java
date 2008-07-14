package com.github.rd4j.expr;

import java.lang.reflect.Type;

public interface TypedReference {
	public Object get();
	public Type getType(); 
	public void set(Object obj);
}
