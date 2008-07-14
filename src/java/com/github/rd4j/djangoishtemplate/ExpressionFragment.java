package com.github.rd4j.djangoishtemplate;

import java.io.IOException;
import java.io.Writer;

import com.github.rd4j.expr.Expression;
import com.github.rd4j.expr.ExpressionUtil;
import com.github.rd4j.expr.TypedReference;

public class ExpressionFragment implements TemplateFragment {
	private final Expression expression;
	
	public ExpressionFragment(String expressionStr) {
		expression = ExpressionUtil.parseExpression(expressionStr);
	}
	
	public void render(Writer w, RenderContext renderContext) throws IOException {
		TypedReference ref = ExpressionUtil.getReferenceFromExpression(renderContext.root, expression);
		w.write(ref.get().toString());
	}

}
