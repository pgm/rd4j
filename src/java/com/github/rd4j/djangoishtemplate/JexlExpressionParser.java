package com.github.rd4j.djangoishtemplate;


import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.JexlHelper;

public class JexlExpressionParser implements ExpressionParser {

	public ParsedExpression parseExpression(String expressionStr) {
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
				throw new RuntimeException("unsupported");
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
