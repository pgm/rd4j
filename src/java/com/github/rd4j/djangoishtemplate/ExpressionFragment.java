package com.github.rd4j.djangoishtemplate;

import java.io.IOException;

import com.github.rd4j.writer.HtmlWriter;

public class ExpressionFragment implements TemplateFragment {
	private final ParsedExpression expression;
	
	public ExpressionFragment(ParsedExpression expression) {
		this.expression = expression;
	}
	
	public void render(HtmlWriter w, RenderContext renderContext) throws IOException {
		Object value = expression.get(renderContext.getRoot());
		w.writeEscaped(value.toString());
	}

}
