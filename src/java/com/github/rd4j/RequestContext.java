package com.github.rd4j;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.github.rd4j.DefaultDispatcher.UrlBinding;

public class RequestContext {
	private final HttpServletRequest httpRequest;
	private final Map<String, String> parameters;
	private final MethodAndParams handler;
	private final UrlBinding urlBinding;

	public RequestContext(HttpServletRequest httpRequest,
			Map<String, String> parameters, MethodAndParams handler,
			UrlBinding urlBinding) {
		super();
		this.httpRequest = httpRequest;
		this.parameters = parameters;
		this.handler = handler;
		this.urlBinding = urlBinding;
	}

	public HttpServletRequest getHttpRequest() {
		return httpRequest;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public MethodAndParams getHandler() {
		return handler;
	}

	public UrlBinding getUrlBinding() {
		return urlBinding;
	}


}
