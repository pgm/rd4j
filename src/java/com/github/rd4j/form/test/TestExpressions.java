package com.github.rd4j.form.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.github.rd4j.TypedMap;
import com.github.rd4j.expr.AttributeExpression;
import com.github.rd4j.expr.Expression;
import com.github.rd4j.expr.ExpressionUtil;
import com.github.rd4j.expr.TypedReference;

public class TestExpressions {
	public static class SampleParentClass {
		public String name;
		public SampleParentClass child;
	}

	TypedMap map;
	
	@Before
	public void setup() {
		map  = new TypedMap();
		map.setType("form1", SampleParentClass.class);
		map.setType("form2", SampleParentClass.class);
	}
	
	@Test
	public void testFieldAccessor() {
		Expression exp1 = new AttributeExpression("form1", null);
		TypedReference ref = ExpressionUtil.getReferenceFromExpression(map, exp1);
		
		SampleParentClass instance = new SampleParentClass();
		ref.set(instance);

		assertTrue(ref.get() == instance);
		assertTrue(map.getValue("form1") == instance);
	}

	@Test
	public void testClassAccessor() {
		Expression exp1 = new AttributeExpression("form2", new AttributeExpression("name", null));
		TypedReference ref = ExpressionUtil.getReferenceFromExpression(map, exp1);
		
		String name = "Jim";
		ref.set(name);

		assertTrue(ref.get() == name);
		assertTrue(((SampleParentClass)map.getValue("form2")).name == name);
	}
	
	@Test 
	public void testNestedClassAccessor() {
		Expression exp1 = new AttributeExpression("form2", 
				new AttributeExpression("child", 
				new AttributeExpression("name", null )));
		TypedReference ref = ExpressionUtil.getReferenceFromExpression(map, exp1);
		
		String name = "Jim";
		ref.set(name);

		assertTrue(ref.get() == name);
		assertTrue(((SampleParentClass)map.getValue("form2")).child.name == name);
	}
	
	@Test 
	public void testParserAttr() {
		AttributeExpression exp1 = (AttributeExpression)ExpressionUtil.parseExpression("alpha.beta.gamma");
		AttributeExpression exp2 = (AttributeExpression)exp1.getNext();
		AttributeExpression exp3 = (AttributeExpression)exp2.getNext();

		assertEquals(".alpha", exp1.toString());
		assertEquals(".beta", exp2.toString());
		assertEquals(".gamma", exp3.toString());
	}
}
