package com.github.rd4j.djangoishtemplate;

import java.io.IOException;

import com.github.rd4j.writer.HtmlWriter;

public class IfFragment implements TemplateFragment {
	private final ParsedExpression predicate;
	private final TemplateFragment trueFragment;
	private final TemplateFragment falseFragment;
	
	public IfFragment(ParsedExpression predicate, TemplateFragment trueFragment, TemplateFragment falseFragment) {
		this.predicate = predicate;
		this.trueFragment = trueFragment;
		this.falseFragment = falseFragment;
	}

	public void render(HtmlWriter w, RenderContext renderContext) throws IOException {
		Object result = predicate.get(renderContext.getRoot());
		
		if(coerceToBoolean(result)) {
			trueFragment.render(w, renderContext);
		} else {
			falseFragment.render(w, renderContext);
		}
	}
	
	protected boolean coerceToBoolean(Object value) {
		return (Boolean)value;
	}

}
