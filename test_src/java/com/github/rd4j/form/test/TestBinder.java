package com.github.rd4j.form.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.github.rd4j.ErrorCollection;
import com.github.rd4j.form.FormBinder;
import com.github.rd4j.form.types.ArrayType;
import com.github.rd4j.form.types.CustomClassType;
import com.github.rd4j.form.types.ListType;
import com.github.rd4j.form.types.MapType;
import com.github.rd4j.form.types.ParameterName;
import com.github.rd4j.form.types.Rd4jType;
import com.github.rd4j.form.types.Types;

public class TestBinder {
	
	@Test
	public void testBasicTypesBind()
	{
		Map<String, String> bindings = new HashMap<String, String>();
		Map<String, Rd4jType> types = new HashMap<String, Rd4jType>();
		ErrorCollection errorCollection = new ErrorCollection();
		
		Map root = new HashMap<String, Object>();
		types.put("string", Types.STRING);
		types.put("integer", Types.INTEGER);
		types.put("double", Types.DOUBLE);
		
		bindings.put("string", "alpha");
		bindings.put("integer", "24");
		bindings.put("double", "23.123");
		
		FormBinder.bind(root, types, bindings, errorCollection);
		
		assertEquals("alpha", root.get("string"));
		assertEquals(24, root.get("integer"));
		assertEquals(23.123, root.get("double"));
	}
	
	public static class ComplexType
	{
		String alpha;
		String beta;
		
		public ComplexType(@ParameterName("alpha") String alpha, @ParameterName("beta") String beta)
		{
			this.alpha = alpha;
			this.beta = beta;
		}
		
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
		Map<String, Rd4jType> types = new HashMap<String, Rd4jType>();
		ErrorCollection errorCollection = new ErrorCollection();
		
		Map root = new HashMap<String, Object>();
		types.put("complex", new CustomClassType(ComplexType.class));
		
		bindings.put("complex.alpha", "a");
		bindings.put("complex.beta", "b");
		
		FormBinder.bind(root, types, bindings, errorCollection);
		
		assertEquals("a", ((ComplexType)root.get("complex")).getAlpha());
		assertEquals("b", ((ComplexType)root.get("complex")).getBeta());
	}
	
	/**
	 * Do we want to support maps that can have non-string keys and values
	 */
	@Test
	public void testMap()
	{
		Map<String, String> bindings = new HashMap<String, String>();
		Map<String, Rd4jType> types = new HashMap<String, Rd4jType>();
		ErrorCollection errorCollection = new ErrorCollection();
		
		Map root = new HashMap<String, Object>();
		types.put("map", new MapType(Types.STRING, Types.STRING));
		
		bindings.put("map[alpha]", "a");
		bindings.put("map[beta]", "b");
		
		FormBinder.bind(root, types, bindings, errorCollection);
		
		assertEquals("a", ((Map)root.get("map")).get("alpha") );
		assertEquals("b", ((Map)root.get("map")).get("beta") );
	}

	@Test
	public void testStringArray()
	{
		Map<String, String> bindings = new HashMap<String, String>();
		Map<String, Rd4jType> types = new HashMap<String, Rd4jType>();
		ErrorCollection errorCollection = new ErrorCollection();
		
		Map root = new HashMap<String, Object>();
		types.put("l", new ArrayType(Types.STRING));
		
		bindings.put("l[1]", "a");
		bindings.put("l[2]", "b");
		
		FormBinder.bind(root, types, bindings, errorCollection);
		
		assertEquals("a", ((String[])root.get("l"))[1] );
		assertEquals("b", ((String[])root.get("l"))[2] );
		assertNull(((String[])root.get("l"))[0] );
	}

	@Test
	public void testComplexArray()
	{
		Map<String, String> bindings = new HashMap<String, String>();
		Map<String, Rd4jType> types = new HashMap<String, Rd4jType>();
		ErrorCollection errorCollection = new ErrorCollection();
		
		Map root = new HashMap<String, Object>();
		types.put("l", new ArrayType(new CustomClassType(ComplexType.class)));
		
		bindings.put("l[1].alpha", "a1");
		bindings.put("l[1].beta", "b1");
		bindings.put("l[2].alpha", "a2");
		bindings.put("l[2].beta", "b2");
		
		FormBinder.bind(root, types, bindings, errorCollection);
		
		assertEqualsComplex("a1", "b1", ((ComplexType[])root.get("l"))[1]);
		assertEqualsComplex("a2", "b2", ((ComplexType[])root.get("l"))[2]);
		assertNull(((ComplexType[])root.get("l"))[0] );
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
		Map<String, Rd4jType> types = new HashMap<String, Rd4jType>();
		
		Map root = new HashMap<String, Object>();
		types.put("l", new ListType(Types.STRING));
		
		bindings.put("l[1]", "a");
		bindings.put("l[2]", "b");
		
		FormBinder.bind(root, types, bindings, errorCollection);
		
		assertEquals("a", ((List)root.get("l")).get(1) );
		assertEquals("b", ((List)root.get("l")).get(2) );
		assertNull(((List)root.get("l")).get(0) );
	}

}

