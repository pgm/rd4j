package com.github.rd4j;

import java.util.Map;

public interface HandlerFactory {
	public MethodAndParams addPath(String beanName, String methodName, Map<String, Object> staticParameters);
}
