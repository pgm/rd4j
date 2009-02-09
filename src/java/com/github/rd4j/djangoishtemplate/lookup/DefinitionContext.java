package com.github.rd4j.djangoishtemplate.lookup;

import com.github.rd4j.djangoishtemplate.ExpressionParser;
import com.github.rd4j.djangoishtemplate.Macro;
import com.github.rd4j.djangoishtemplate.Template;

public class DefinitionContext {
	Lookup<Template> templates;
	Lookup<Macro> macros;

	protected void setLookups(Lookup<Macro> macros, Lookup<Template> templates) {
		this.macros = macros;
		this.templates = templates;
	}
	
	public DefinitionContext(Lookup<Macro> macros, Lookup<Template> templates) {
		super();
		setLookups(macros, templates);
	}

	public Template getTemplate(String name) {
		return this.templates.get(name);
	}
	
	public Macro getMacro(String name) {
		return this.macros.get(name);
	}
}
