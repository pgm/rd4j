package com.github.rd4j.djangoishtemplate;

import java.io.IOException;
import java.io.Writer;

public class BlockFragment  implements TemplateFragment {
	private final String name;
	
	public BlockFragment(String name) {
		this.name = name;
	}
	
	public void render(Writer w, RenderContext renderContext) throws IOException {
		TemplateFragment body = renderContext.getNamedBlock(name);
		if(body == null) {
			throw new RuntimeException("No block named "+name);
		}
		body.render(w, renderContext);
	}
}
