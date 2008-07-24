package com.github.rd4j.djangoishtemplate.lookup;

import java.util.HashMap;
import java.util.Map;

import com.github.rd4j.djangoishtemplate.Macro;
import com.github.rd4j.djangoishtemplate.Template;

public class DefaultDefinitionContext extends DefinitionContext {

	final Map<String, Macro> macros;
	final Map<String, Template> templates;
	
	public DefaultDefinitionContext() {
		super(null,null);

		macros = new HashMap<String, Macro>();
		templates = new HashMap<String, Template>();
		this.setLookups(new MapLookupAdapter<Macro>(macros), new MapLookupAdapter<Template>(templates));
	}
	
	public void addMacro(String name, Macro macro) {
		macros.put(name, macro);
	}

	public void addTemplate(String name, Template template) {
		templates.put(name, template);
	}
}
