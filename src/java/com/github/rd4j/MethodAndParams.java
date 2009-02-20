/**
 * 
 */
package com.github.rd4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.github.rd4j.analysis.MethodParameter;

public class MethodAndParams {
	protected final Object instance;
	protected final Method method;
	protected final MethodParameter parameters[];

	public MethodAndParams(Object instance, Method method, MethodParameter[] parameters) {
		super();
		this.instance = instance;
		this.method = method;
		this.parameters = parameters;
	}

	public Method getMethod() {
		return method;
	}

	public MethodParameter[] getParameters() {
		return parameters;
	}
	
	public Resolution invoke(Object ... args) 
	{
		try {
			return (Resolution)method.invoke(this.instance, args);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
	public Object getTarget()
	{
		return this.instance;
	}
}