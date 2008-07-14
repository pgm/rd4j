package com.github.rd4j;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.github.rd4j.DefaultDispatcher.UrlBinding;
import com.github.rd4j.analysis.MethodParameter;

public class RequestContext {
	public HttpServletRequest httpRequest;
	public Map<String, String> parameters;
	public BoundMethod handler;
	public UrlBinding urlBinding;
	
	public MethodParameter[] getMethodParameters() {
		return handler.getMethodParameters();
	}
}
