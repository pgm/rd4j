package com.github.rd4j;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.antlr.stringtemplate.NoIndentWriter;
import org.antlr.stringtemplate.PathGroupLoader;
import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateErrorListener;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.StringTemplateWriter;
import org.apache.log4j.Logger;

public class StringTemplateResolution implements Resolution {
	private static final Logger log = Logger.getLogger(StringTemplateResolution.class);

	protected StringTemplate template;
	
	public StringTemplateResolution(String groupName, Map<String,Object> attributes) {
		StringTemplateErrorListener listener = new StringTemplateErrorListener() {

			public void error(String message, Throwable t) {
				log.error(message, t);
			}

			public void warning(String message) {
				log.warn(message);
			}
			
		};
		PathGroupLoader loader = new PathGroupLoader("templates", listener);
		
		StringTemplateGroup group = loader.loadGroup(groupName);
		template = group.getInstanceOf("staticExample", attributes);
	}
	
	public void go(HttpServletResponse response) throws IOException {
		OutputStream os = response.getOutputStream();
		OutputStreamWriter w = new OutputStreamWriter(os);
		StringTemplateWriter stw = new NoIndentWriter(w);
		template.write(stw);
		w.close();
	}

}
