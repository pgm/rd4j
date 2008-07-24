package com.github.rd4j.djangoishtemplate.lookup;

import java.util.Map;


public class MapLookupAdapter<T> implements Lookup<T>{
	final Map<String, T> dictionary;
	
	public MapLookupAdapter(Map<String, T> dictionary) {
		super();
		this.dictionary = dictionary;
	}

	public T get(String name) {
		if(!dictionary.containsKey(name))
			throw new KeyNotFoundException(name);
		
		T value = dictionary.get(name);
		return value;
	}
}
