package com.github.rd4j.djangoishtemplate;

import java.io.IOException;
import java.io.Writer;

public class StaticFragment implements TemplateFragment {
	private final String content;

	public StaticFragment(String content) {
		this.content = content;
	}
	
	public void render(Writer w, RenderContext renderContext) throws IOException {
		w.write(content);
	}
}
