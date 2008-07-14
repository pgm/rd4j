package com.github.rd4j;

import java.lang.reflect.Method;

import com.github.rd4j.analysis.MethodParameter;

public class BoundMethod {
	private final MethodParameter[] parmeters;
	private final Object target;
	private final Method method;
	
	public BoundMethod(MethodParameter[] parmeters, Object target, Method method) {
		super();
		this.parmeters = parmeters;
		this.target = target;
		this.method = method;
	}

	public MethodParameter[] getMethodParameters() {
		return this.parmeters;
	}
	
	public Resolution invoke(Object[]args) {
		Object result;
		try {
			result = this.method.invoke(target, args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return (Resolution)result;
	}
	
	public Object getTarget() {
		return target;
	}
}
