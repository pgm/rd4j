package com.github.rd4j.djangoishtemplate;

import java.io.IOException;
import java.util.Map;

import com.github.rd4j.expr.Expression;
import com.github.rd4j.writer.HtmlWriter;

public interface Macro {
	public void render(HtmlWriter writer, Map<String, Expression> bindings, TemplateFragment body, RenderContext renderContext) throws IOException;
}
