package com.github.rd4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

public class StandardRequestHandlers {
	public Resolution pageNotFound() {
		return new Resolution() {
			public void go(ServletContext context, HttpServletResponse response) throws IOException {
				response.sendError(404, "Page not found");
			}
		};
	}
	
	public static final String SERVE_STATIC_FILE_METHOD = "serveStaticFile";
	
	public Resolution serveStaticFile(String filesRoot, ServletContext servletContext, String path) {
		// add some validation of path
		final InputStream is = servletContext.getResourceAsStream(filesRoot+path);
		
		if(is == null) {
			return pageNotFound();
		}
		
		return new Resolution() {
			public void go(ServletContext context, HttpServletResponse response) throws IOException {
				OutputStream outputStream = response.getOutputStream();

				byte buffer [] = new byte[8*1204];
				while(true) {
					int len = is.read(buffer);
					if(len <= 0) 
						break;
					outputStream.write(buffer, 0, len);
				}

				outputStream.close();
			}
		};
	}
	
	public static final String SERVE_TEMPLATED_FILE_METHOD = "serveTemplatedFile";
	
	public Resolution serveTemplatedFile(ServletContext context, String templateName) {
		return new DjangoTemplateResolution(context, templateName+".rd4j");
	}
}
