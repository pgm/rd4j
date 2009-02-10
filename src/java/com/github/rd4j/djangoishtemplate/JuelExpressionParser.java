package com.github.rd4j.djangoishtemplate;

import java.lang.reflect.Method;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.FunctionMapper;
import javax.el.ValueExpression;
import javax.el.VariableMapper;

import com.sun.el.ExpressionFactoryImpl;

import de.odysseus.el.util.SimpleResolver;

public abstract class JuelExpressionParser implements ExpressionParser {
	/*
	ExpressionFactory factory = new ExpressionFactoryImpl();

	FunctionMapper functionMapper = new FunctionMapper() 
	{
		@Override
		public Method resolveFunction(String prefix, String localname) {
			return null;
		}
	};

	VariableMapper variableMapper = new VariableMapper() 
	{
		@Override
		public ValueExpression resolveVariable(String name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ValueExpression setVariable(String name, ValueExpression expr) {
			// TODO Auto-generated method stub
			return null;
		}
	};
	
	ELResolver elResolver = new SimpleResolver();

	class ContextAdapter extends ELContext
	{
		@Override
		public ELResolver getELResolver() {
			return elResolver;
		}

		@Override
		public FunctionMapper getFunctionMapper() {
			// TODO Auto-generated method stub
			return functionMapper;
		}

		@Override
		public VariableMapper getVariableMapper() {
			// TODO Auto-generated method stub
			return variableMapper;
		}
	}
	
	public ParsedExpression parseExpression(final String expression) {
		return new ParsedExpression() {
			public Object get(Object context) {
				de.odysseus.el.util.SimpleContext elContext = new de.odysseus.el.util.SimpleContext();
				ValueExpression valueExpression = factory.createValueExpression(elContext, "${"+expression+"}", Object.class);
				return valueExpression.getValue(elContext);
			}

			public void set(Object context, Object value) {
				de.odysseus.el.util.SimpleContext elContext = new de.odysseus.el.util.SimpleContext();
				ValueExpression valueExpression = factory.createValueExpression(elContext, "${"+expression+"}", Object.class);
				valueExpression.setValue(elContext, value);
			}
			
		};
		
	}
*/
}
