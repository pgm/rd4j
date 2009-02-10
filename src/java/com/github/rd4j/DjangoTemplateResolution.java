package com.github.rd4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import com.github.rd4j.djangoishtemplate.ExpressionContext;
import com.github.rd4j.djangoishtemplate.ExpressionParser;
import com.github.rd4j.djangoishtemplate.Template;
import com.github.rd4j.djangoishtemplate.lookup.DefinitionContext;
import com.github.rd4j.djangoishtemplate.lookup.Lookup;
import com.github.rd4j.writer.StreamHtmlWriter;

public class DjangoTemplateResolution implements Resolution {
	ExpressionContext root;
	String name;
	ServletContext context;
	ExpressionParser expressionParser;

	Lookup<Template> templateLookup = new Lookup<Template>() {
		public Template get(String name) {
			return readTemplateFromFile(name);
		} 
	};
	
	public DjangoTemplateResolution(ServletContext servletContext, String name, ExpressionContext root) {
		if(servletContext == null)
			throw new RuntimeException("Servlet context cannot be null");
		
		this.context = servletContext;
		this.name = name;
		this.root = root;
	}
	
	public DjangoTemplateResolution addAttribute(String name, Object value) {
		root.put(name, value);
		return this;
	}

	Template readTemplateFromFile(String filename) {
		try { 
			String fullFilename = "/templates/"+filename;
			InputStream inputStream = context.getResourceAsStream(fullFilename);
			if(inputStream == null)
			{
				throw new RuntimeException("Could not find "+fullFilename);
			}
			
			Reader reader = new InputStreamReader(inputStream);

			Template t = new Template(filename, reader, new DefinitionContext(null, templateLookup), expressionParser);
			
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
