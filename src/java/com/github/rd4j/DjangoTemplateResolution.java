package com.github.rd4j;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

	Template readTemplateFromFile(String filename) {
		try { 
			FileReader reader = new FileReader("templates/"+filename);
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
					return readTemplateFromFile(name);
				} 
			} );
			
			return t;
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public void go(HttpServletResponse response) throws IOException {
		Template t = readTemplateFromFile(name);
		t.renderTemplate(response.getWriter(), root);
	}

}
