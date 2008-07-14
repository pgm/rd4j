package com.github.rd4j.analysis;

import org.objectweb.asm.Type;


public class MethodParameter {
	private final String name;
	private final Class<?> type;
	public MethodParameter(String name, Class<?> type) {
		super();
		this.name = name;
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public Class<?> getType() {
		return type;
	}
}
