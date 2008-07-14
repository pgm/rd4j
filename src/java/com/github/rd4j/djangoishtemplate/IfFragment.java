package com.github.rd4j.djangoishtemplate;

import java.io.IOException;
import java.io.Writer;

import com.github.rd4j.expr.Expression;
import com.github.rd4j.expr.ExpressionUtil;
import com.github.rd4j.expr.TypedReference;

public class IfFragment implements TemplateFragment {
	private final Expression predicate;
	private final TemplateFragment trueFragment;
	private final TemplateFragment falseFragment;
	
	public IfFragment(String predicateStr, TemplateFragment trueFragment, TemplateFragment falseFragment) {
		predicate = ExpressionUtil.parseExpression(predicateStr);
		this.trueFragment = trueFragment;
		this.falseFragment = falseFragment;
	}

	public void render(Writer w, RenderContext renderContext) throws IOException {
		TypedReference ref = ExpressionUtil.getReferenceFromExpression(renderContext.root, predicate);
		Object result = ref.get();
		
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
