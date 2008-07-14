package com.github.rd4j;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

public class DummyResolution implements Resolution {

	public void go(HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		out.println("<html><head>right</head><body>okay: ");
		out.println("right");
		out.println("</body></html>");
		out.close();
	}

}
