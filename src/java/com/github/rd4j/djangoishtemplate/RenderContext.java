package com.github.rd4j.djangoishtemplate;


public class RenderContext {
	final Object root;
	final Template template;

	
	
	public RenderContext(Template template, Object root) {
		super();
		this.root = root;
		this.template = template;
	}



	public TemplateFragment getNamedBlock(String name) {
		return template.getBlock(name);
	}
}
