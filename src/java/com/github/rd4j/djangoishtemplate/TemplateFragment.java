package com.github.rd4j.djangoishtemplate;

import java.io.IOException;

import com.github.rd4j.writer.HtmlWriter;

public interface TemplateFragment {
	public void render(HtmlWriter w, RenderContext renderContext) throws IOException;
}
