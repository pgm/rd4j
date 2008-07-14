package com.github.rd4j;

public interface RequestInterceptor {
	public Resolution intercept(RequestContext ctx);
}
