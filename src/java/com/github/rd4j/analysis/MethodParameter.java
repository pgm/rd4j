package com.github.rd4j.analysis;

import java.lang.reflect.Type;



public class MethodParameter {
	private final String name;
	private final Type type;

	public MethodParameter(String name, Type type) {
		super();
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public Type getType() {
		return type;
	}
}
