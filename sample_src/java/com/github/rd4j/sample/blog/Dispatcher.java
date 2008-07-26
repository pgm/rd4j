package com.github.rd4j.sample.blog;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import com.github.rd4j.DefaultDispatcher;
import com.github.rd4j.StandardRequestHandlers;
import com.github.rd4j.SuperDispatch;

public class Dispatcher extends DefaultDispatcher {

	protected ServletContext servletContext;
	
	public void init(SuperDispatch servlet, ServletConfig config) throws ServletException {
		servletContext = config.getServletContext();

		addPathesForPackage("com.github.rd4j.sample.blog");
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("servletContext", this.servletContext);
		params.put("filesRoot", "/css/");
		addPath("/css/(?P<path>.*)", "", StandardRequestHandlers.class, StandardRequestHandlers.SERVE_STATIC_FILE_METHOD, params);

		addPath("/(?P<templateName>showNewPostForm)", "", StandardRequestHandlers.class, StandardRequestHandlers.SERVE_TEMPLATED_FILE_METHOD);
		
		servlet.addPrebindInterceptor(new InjectorInterceptor(servletContext));
	}

	
}
