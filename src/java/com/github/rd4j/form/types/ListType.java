package com.github.rd4j.form.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListType implements IndexedType {
	final Rd4jType elementType;
	
	public ListType(Rd4jType elementType) 
	{
		this.elementType = elementType;
	}
	
	public Rd4jType getElementType() {
		return elementType;
	}

	public BasicType getIndexType() {
		return Types.INTEGER;
	}

	public Object create() {
		return new ArrayList();
	}

	public void set(Object collection, Object index, Object value) {
		int i = ((Number)index).intValue();
		List list = ((List)collection);
		
		// enlarge list until it contains index
		while(list.size() <= i)
		{
			list.add(null);
		}
		
		list.set(i, value);
	}
	
	public Class<?> getJavaClass() {
		return Map.class;
	}
}
