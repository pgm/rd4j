package com.github.rd4j.djangoishtemplate.test;

import org.junit.Test;

import com.github.rd4j.djangoishtemplate.ExpressionContext;
import com.github.rd4j.djangoishtemplate.JexlExpressionParser;
import com.github.rd4j.djangoishtemplate.ParsedExpression;

import static org.junit.Assert.assertEquals;

public class TestJexlExpressionParser {
	public static class SampleClass
	{
		public int getFive() {
			return 5;
		}
	}
	
	@Test
	public void testExpressionParser() 
	{
		JexlExpressionParser parser = new JexlExpressionParser();
		ParsedExpression expression = parser.parseExpression("two+three");
		ExpressionContext context = parser.createContext();
		context.put("two", 2);
		context.put("three", 3);
		assertEquals(5, expression.get(context));
	}
	
	public void testObjectProperty()
	{
		JexlExpressionParser parser = new JexlExpressionParser();
		ParsedExpression expression = parser.parseExpression("object.five");
		ExpressionContext context = parser.createContext();
		context.put("object", new SampleClass());
		assertEquals(5, expression.get(context));
	}
}
