package com.github.rd4j.djangoishtemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.github.rd4j.expr.Expression;
import com.github.rd4j.expr.ExpressionUtil;
import com.github.rd4j.expr.TypedReference;
import com.github.rd4j.writer.HtmlWriter;

public class ForFragment implements TemplateFragment {
	private final Expression indexExpr;
	private final Expression collectionExpr;
	private final TemplateFragment body;
	
	public ForFragment(String indexExprStr, String collectionExprStr, TemplateFragment body) {
		indexExpr = ExpressionUtil.parseExpression(indexExprStr);
		collectionExpr = ExpressionUtil.parseExpression(collectionExprStr);
		this.body = body;
	}
	
	public void render(HtmlWriter w, RenderContext renderContext) throws IOException {
		TypedReference indexRef = ExpressionUtil.getReferenceFromExpression(renderContext.root, indexExpr);
		TypedReference collectionRef = ExpressionUtil.getReferenceFromExpression(renderContext.root, collectionExpr);
		
		Iterator<?> it = coerceToIterator(collectionRef.get());
		while(it.hasNext()) {
			indexRef.set(it.next());
			body.render(w, renderContext);
		}
	}

	protected Iterator<?> coerceToIterator(Object value) {
		Class <?> valueClass = value.getClass();

		Iterator<?> it;
		
		if(valueClass.isArray()) {
			List<?> l = Arrays.asList(((Object[])value));
			it = l.iterator();
		} else if(value instanceof Iterator) {
			it = (Iterator<?>)value;
		} else if(value instanceof Collection) {
			it = ((Collection<?>)value).iterator();
		} else {
			throw new RuntimeException("Could not coerce "+valueClass+" into iterator");
		}
		
		return it;
	}
	
}
