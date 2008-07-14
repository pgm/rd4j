package com.github.rd4j;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;


public class FreeMarkerResolution implements Resolution{
	Template template;
	Map<String,Object> attributes;

	public FreeMarkerResolution(String groupName) {
		this(groupName, new HashMap<String,Object>());
	}
	
	public FreeMarkerResolution(String groupName, Map<String,Object> attributes) {
		Configuration cfg = new Configuration();
		try {
			cfg.setDirectoryForTemplateLoading(
			        new File("templates"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		try {
			template = cfg.getTemplate(groupName);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		this.attributes = attributes;
	}
	
	public void go(HttpServletResponse response) throws IOException {
		OutputStream os = response.getOutputStream();
		OutputStreamWriter w = new OutputStreamWriter(os);
		try {
			template.process(attributes, w);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		w.close();
	}
	
	public FreeMarkerResolution addAttribute(String name, Object value) {
		attributes.put(name, value);
		return this;
	}

}
