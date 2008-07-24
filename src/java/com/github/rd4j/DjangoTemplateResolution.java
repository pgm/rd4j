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
import com.github.rd4j.djangoishtemplate.lookup.DefinitionContext;
import com.github.rd4j.djangoishtemplate.lookup.Lookup;
import com.github.rd4j.writer.StreamHtmlWriter;

public class DjangoTemplateResolution implements Resolution {
	Map<String, Object> root;
	String name;
	ServletContext context;

	Lookup<Template> templateLookup = new Lookup<Template>() {
		public Template get(String name) {
			return readTemplateFromFile(name);
		} 
	};
	
	public DjangoTemplateResolution(ServletContext context, String name) {
		this(context, name, new HashMap<String,Object>());
	}
	
	public DjangoTemplateResolution(ServletContext context, String name, Map<String, Object> root) {
		this.context = context;
		this.name = name;
		this.root = root;
	}
	
	public DjangoTemplateResolution addAttribute(String name, Object value) {
		root.put(name, value);
		return this;
	}

	Template readTemplateFromFile(String filename) {
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

			Template t = new Template(sb.toString(), new DefinitionContext(null, templateLookup));
			
			inputStream.close();
			
			return t;
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public void go(ServletContext context, HttpServletResponse response) throws IOException {
		Template t = readTemplateFromFile(name);
		t.renderTemplate(new StreamHtmlWriter(response.getWriter()), root);
	}

}
