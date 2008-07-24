package com.github.rd4j;

import javax.servlet.ServletContext;

public class DjangoTemplateResolutionFactory {
	final ServletContext context;

	public DjangoTemplateResolutionFactory(ServletContext context) {
		super();
		this.context = context;
	}
	
	public DjangoTemplateResolution create(String filename) {
		return new DjangoTemplateResolution(context, filename);
	}
}
