package com.github.rd4j.djangoishtemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.github.rd4j.writer.HtmlWriter;

public class ForFragment implements TemplateFragment {
	private final ParsedExpression indexExpr;
	private final ParsedExpression collectionExpr;
	private final TemplateFragment body;
	
	public ForFragment(ParsedExpression indexExpr, ParsedExpression collectionExpr, TemplateFragment body) {
		this.indexExpr = indexExpr;
		this.collectionExpr = collectionExpr;
		this.body = body;
	}
	
	public void render(HtmlWriter w, RenderContext renderContext) throws IOException {
		Iterator<?> it = coerceToIterator(this.collectionExpr.get(renderContext.getRoot()));
		while(it.hasNext()) {
			indexExpr.set(renderContext.getRoot(), it.next());
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
