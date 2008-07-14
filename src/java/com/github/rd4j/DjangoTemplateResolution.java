package com.github.rd4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import com.github.rd4j.djangoishtemplate.Template;
import com.github.rd4j.djangoishtemplate.TemplateResolver;

public class DjangoTemplateResolution implements Resolution {
	Map<String, Object> root;
	String name;

	public DjangoTemplateResolution(String name) {
		this(name, new HashMap<String,Object>());
	}
	
	public DjangoTemplateResolution(String name, Map<String, Object> root) {
		this.name = name;
		this.root = root;
	}
	
	public DjangoTemplateResolution addAttribute(String name, Object value) {
		root.put(name, value);
		return this;
	}

	Template readTemplateFromFile(final ServletContext context, String filename) {
		try { 
			InputStream inputStream = context.getResourceAsStream("/templates/"+filename);
			Reader reader = new InputStreamReader(inputStream);
			StringBuilder sb = new StringBuilder();
			char buffer[] = new char[1000];
			while(true) {
				int len = reader.read(buffer);
				if(len <= 0)
					break;
	
				sb.append(buffer, 0, len);
			}
			Template t = new Template(sb.toString(), new TemplateResolver() {
				public Template findTemplate(String name) {
					return readTemplateFromFile(context, name);
				} 
			} );
			
			inputStream.close();
			
			return t;
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public void go(ServletContext context, HttpServletResponse response) throws IOException {
		Template t = readTemplateFromFile(context, name);
		t.renderTemplate(response.getWriter(), root);
	}

}
