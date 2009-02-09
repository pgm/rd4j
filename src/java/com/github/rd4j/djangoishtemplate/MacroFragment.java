package com.github.rd4j.djangoishtemplate;

import java.io.IOException;
import java.util.Map;

import com.github.rd4j.writer.HtmlWriter;

public class MacroFragment implements TemplateFragment {
	protected final TemplateFragment body;
	protected final Map<String, ParsedExpression> bindings;
	protected final Macro macro;

	public MacroFragment(Map<String, ParsedExpression> bindings,
			TemplateFragment body, Macro macro) {
		super();
		this.bindings = bindings;
		this.body = body;
		this.macro = macro;
	}

	public void render(HtmlWriter writer, RenderContext renderContext)
			throws IOException {
		macro.render(writer, bindings, body, renderContext);
	}
}
