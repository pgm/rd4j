package com.github.rd4j.djangoishtemplate;


import java.util.HashMap;
import java.util.Map;

import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.JexlHelper;

public class JexlExpressionParser implements ExpressionParser {

	public ParsedExpression parseExpression(final String expressionStr) {
		final Expression expression;
		
		try {
			expression = ExpressionFactory.createExpression( expressionStr );
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return new ParsedExpression() 
		{
			public Object get(ExpressionContext context) {
				try {
					return expression.evaluate(((JexlExpressionContext)context).getContext());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

			public void set(ExpressionContext context, Object value) {
				// just a heuristic of expressions we cannot set.  Certainly not exhaustive
				if(expressionStr.contains(".") || expressionStr.contains("[") || expressionStr.contains("]"))
				{
					throw new RuntimeException("cannot set expression="+expressionStr);
				}
				((JexlExpressionContext)context).put(expressionStr, value);
			}
		};
	}

	public class JexlExpressionContext implements ExpressionContext {
		final JexlContext context = JexlHelper.createContext();
		
		public JexlContext getContext() {
			return context;
		}

		public void put(String name, Object value) {
			context.getVars().put(name, value);
		}
	}
	
	public ExpressionContext createContext() {
		return new JexlExpressionContext();
	}
}
