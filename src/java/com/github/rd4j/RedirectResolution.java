package com.github.rd4j;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

public class RedirectResolution implements Resolution {
	final String redirectUrl;
	
	public RedirectResolution(String redirectUrl) {
		super();
		this.redirectUrl = redirectUrl;
	}

	public void go(ServletContext context, HttpServletResponse response) throws IOException {
		response.sendRedirect("/context"+redirectUrl);
	}

}
