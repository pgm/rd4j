package com.github.rd4j;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

public interface Resolution {
	public void go(ServletContext context, HttpServletResponse response) throws IOException;
}
