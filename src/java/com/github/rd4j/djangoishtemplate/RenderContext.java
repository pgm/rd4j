package com.github.rd4j.djangoishtemplate;


public class RenderContext {
	private final ExpressionContext root;
	private final Template template;
	
	public RenderContext(Template template, ExpressionContext root) {
		super();
		this.root = root;
		this.template = template;
	}

	public TemplateFragment getNamedBlock(String name) {
		return template.getBlock(name);
	}
	
	public ExpressionContext getRoot() {
		return this.root;
	}
}
