package com.github.rd4j.form.types;

import java.util.HashMap;
import java.util.Map;

public class MapType implements IndexedType {
	final Rd4jType elementType;
	final BasicType indexType;
	
	public MapType(BasicType indexType, Rd4jType elementType) 
	{
		this.elementType = elementType;
		this.indexType = indexType;
	}
	
	public Rd4jType getElementType() {
		return elementType;
	}

	public BasicType getIndexType() {
		return indexType;
	}
	
	public Object create() {
		return new HashMap();
	}

	public void set(Object collection, Object index, Object value) {
		((Map)collection).put(index, value);
	}

	public Class<?> getJavaClass() {
		return Map.class;
	}
}
