package com.github.rd4j.form.types;

import java.lang.reflect.Array;

public class ArrayType implements IndexedType {
	final Rd4jType elementType;
	final Class<?> clazz;
	
	public ArrayType(Rd4jType elementType) 
	{
		this.elementType = elementType;
		
		clazz = Array.newInstance(elementType.getJavaClass(), 0).getClass();
	}
	
	public Rd4jType getElementType() {
		return elementType;
	}

	public BasicType getIndexType() {
		return Types.INTEGER;
	}

	public Object create() {
		return Array.newInstance(elementType.getJavaClass(), 100);
	}

	public void set(Object collection, Object index, Object value) {
		Array.set(collection, ((Number)index).intValue(), value);
	}

	public Class<?> getJavaClass() {
		return clazz;
	}
}
