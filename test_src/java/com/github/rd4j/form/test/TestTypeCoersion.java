package com.github.rd4j.form.test;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.github.rd4j.form.types.ListType;
import com.github.rd4j.form.types.MapType;
import com.github.rd4j.form.types.Rd4jType;
import com.github.rd4j.form.types.Types;

public class TestTypeCoersion {
	public static class SampleClass
	{
		public List<Integer> list;
		public Map<String,Integer> map;
		public Integer intField;
		public String stringField;
		public Double doubleField;
	}

	public Rd4jType getTypeFromField(String name) throws Exception
	{
		return Types.coerceToRd4jType(SampleClass.class.getField(name).getGenericType());
	}
	
	@Test
	public void testBasicConversions() throws Exception
	{
		assertEquals(Types.INTEGER, getTypeFromField("intField"));
		assertEquals(Types.STRING, getTypeFromField("stringField"));
		assertEquals(Types.DOUBLE, getTypeFromField("doubleField"));
	}
	
	@Test
	public void testMapConversion() throws Exception
	{
		Rd4jType _mapType = getTypeFromField("map");
		MapType mapType = (MapType)_mapType;
		assertEquals(Types.INTEGER, mapType.getElementType());
		assertEquals(Types.STRING, mapType.getIndexType());
	}
	
	@Test
	public void testListConversion() throws Exception
	{
		Rd4jType _listType = getTypeFromField("list");
		ListType listType = (ListType)_listType;
		assertEquals(Types.INTEGER, listType.getElementType());
		assertEquals(Types.INTEGER, listType.getIndexType());
	}
}
