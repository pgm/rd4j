package com.github.rd4j.djangoishtemplate;

import java.io.IOException;
import java.io.Writer;

public interface TemplateFragment {
	public void render(Writer w, RenderContext renderContext) throws IOException;
}
