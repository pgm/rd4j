package com.github.rd4j;

import javax.servlet.ServletContext;

import com.github.rd4j.djangoishtemplate.ExpressionParser;
import com.github.rd4j.djangoishtemplate.JexlExpressionParser;

public class DjangoTemplateResolutionFactory {
	final ServletContext context;
	final ExpressionParser expressionParser = new JexlExpressionParser();

	public DjangoTemplateResolutionFactory(ServletContext context) {
		super();
		this.context = context;
	}
	
	public DjangoTemplateResolution create(String filename) {
		return new DjangoTemplateResolution(context, filename, expressionParser.createContext(), this.expressionParser);
	}
}
