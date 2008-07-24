package com.github.rd4j.djangoishtemplate;

import java.io.IOException;

import com.github.rd4j.writer.HtmlWriter;

public class StaticFragment implements TemplateFragment {
	private final String content;

	public StaticFragment(String content) {
		this.content = content;
	}
	
	public void render(HtmlWriter w, RenderContext renderContext) throws IOException {
		w.writeRaw(content);
	}
}
