package com.github.rd4j;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.github.rd4j.expr.TypedReference;

public class TypedMap {
	protected Map<String, Object> values = new HashMap<String,Object>();
	protected Map<String, Type> types = new HashMap<String, Type>();
	
	class TypedMapReference implements TypedReference {
		final String keyName;
		
		public TypedMapReference(String keyName) {
			this.keyName = keyName;
		}
		
		public Object get() {
			if(!types.containsKey(keyName)) {
				throw new RuntimeException("does not have member "+keyName);
			}
			return values.get(keyName);
		}

		public Type getType() {
			return types.get(keyName);
		}

		public void set(Object obj) {
			Type type = types.get(keyName);
			// add type check here
			setValue(keyName, obj);
		}
	}

	public void setValue(String name, Object value) {
		if(!types.containsKey(name)) {
			types.put(name, value.getClass());
		}
		values.put(name, value);
	}
	
	public void setType(String name, Type type) {
		types.put(name, type);
	}
	
	public TypedReference getReference(String name) {
		return new TypedMapReference(name);
	}
	
	public Object getValue(String name) {
		if(!containsProperty(name)){
			throw new RuntimeException("property "+name+" not in map");
		}
		return values.get(name);
	}
	
	public boolean containsProperty(String name) {
		return types.containsKey(name);
	}
}
