package com.github.rd4j;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public interface Resolution {
	public void go(HttpServletResponse response) throws IOException;
}
