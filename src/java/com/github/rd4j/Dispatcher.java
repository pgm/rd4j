package com.github.rd4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

public interface Dispatcher {
	public RequestContext getMethod(String path, HttpServletRequest request);
	public void init(SuperDispatch servlet, ServletConfig config) throws ServletException;
}
