package com.github.rd4j.form.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.github.rd4j.ErrorCollection;
import com.github.rd4j.TypedMap;
import com.github.rd4j.form.FormBinder;

public class TestBinder {
	
	@Test
	public void testBasicTypesBind()
	{
		Map<String, String> bindings = new HashMap<String, String>();
		ErrorCollection errorCollection = new ErrorCollection();
		
		TypedMap root = new TypedMap();
		root.setType("string", String.class);
		root.setType("integer", Integer.class);
		root.setType("double", Double.class);
		
		bindings.put("string", "alpha");
		bindings.put("integer", "24");
		bindings.put("double", "23.123");
		
		FormBinder.bind(root, bindings, errorCollection);
		
		assertEquals("alpha", root.getValue("string"));
		assertEquals(24, root.getValue("integer"));
		assertEquals(23.123, root.getValue("double"));
	}
	
	public static class ComplexType
	{
		String alpha;
		String beta;
		
		public String getAlpha() {
			return alpha;
		}
		public void setAlpha(String alpha) {
			this.alpha = alpha;
		}
		public String getBeta() {
			return beta;
		}
		public void setBeta(String beta) {
			this.beta = beta;
		}
	}
	
	@Test
	public void testTypesWithSetters()
	{
		Map<String, String> bindings = new HashMap<String, String>();
		ErrorCollection errorCollection = new ErrorCollection();
		
		TypedMap root = new TypedMap();
		root.setType("complex", ComplexType.class);
		
		bindings.put("complex.alpha", "a");
		bindings.put("complex.beta", "b");
		
		FormBinder.bind(root, bindings, errorCollection);
		
		assertEquals("a", ((ComplexType)root.getValue("complex")).getAlpha());
		assertEquals("b", ((ComplexType)root.getValue("complex")).getBeta());
	}
	
	/**
	 * Do we want to support maps that can have non-string keys and values
	 */
	@Test
	public void testMap()
	{
		Map<String, String> bindings = new HashMap<String, String>();
		ErrorCollection errorCollection = new ErrorCollection();
		
		TypedMap root = new TypedMap();
		root.setType("map", Map.class);
		
		bindings.put("map[alpha]", "a");
		bindings.put("map[beta]", "b");
		
		FormBinder.bind(root, bindings, errorCollection);
		
		assertEquals("a", ((Map)root.getValue("map")).get("alpha") );
		assertEquals("b", ((Map)root.getValue("map")).get("beta") );
	}

	@Test
	public void testStringArray()
	{
		Class<?> stringArrayClass = (new String[0]).getClass();

		Map<String, String> bindings = new HashMap<String, String>();
		ErrorCollection errorCollection = new ErrorCollection();
		
		TypedMap root = new TypedMap();
		root.setType("l", stringArrayClass);
		
		bindings.put("l[1]", "a");
		bindings.put("l[2]", "b");
		
		FormBinder.bind(root, bindings, errorCollection);
		
		assertEquals("a", ((String[])root.getValue("l"))[1] );
		assertEquals("b", ((String[])root.getValue("l"))[2] );
		assertNull(((String[])root.getValue("l"))[0] );
	}

	@Test
	public void testComplexArray()
	{
		Class<?> stringArrayClass = (new String[0]).getClass();

		Map<String, String> bindings = new HashMap<String, String>();
		ErrorCollection errorCollection = new ErrorCollection();
		
		TypedMap root = new TypedMap();
		root.setType("l", stringArrayClass);
		
		bindings.put("l[1].alpha", "a1");
		bindings.put("l[1].beta", "b1");
		bindings.put("l[2].alpha", "a1");
		bindings.put("l[2].beta", "b1");
		
		FormBinder.bind(root, bindings, errorCollection);
		
		assertEqualsComplex("a1", "b1", ((ComplexType[])root.getValue("l"))[1]);
		assertEqualsComplex("a2", "b2", ((ComplexType[])root.getValue("l"))[2]);
		assertNull(((ComplexType[])root.getValue("l"))[0] );
	}
	
	private void assertEqualsComplex(String string, String string2,
			ComplexType complexType) {
		assertEquals(string, complexType.getAlpha());
		assertEquals(string2, complexType.getBeta());
	}

	@Test
	public void testList()
	{
		Map<String, String> bindings = new HashMap<String, String>();
		ErrorCollection errorCollection = new ErrorCollection();
		
		TypedMap root = new TypedMap();
		root.setType("l", List.class);
		
		bindings.put("l[1]", "a");
		bindings.put("l[2]", "b");
		
		FormBinder.bind(root, bindings, errorCollection);
		
		assertEquals("a", ((List)root.getValue("l")).get(1) );
		assertEquals("b", ((List)root.getValue("l")).get(2) );
		assertNull(((List)root.getValue("l")).get(0) );
	}

}

