package com.github.rd4j.djangoishtemplate.lookup;

public interface Lookup<T> {
	public T get(String name);
	
	static class KeyNotFoundException extends RuntimeException {
		final String key;

		public KeyNotFoundException(String key) {
			super("Undefined: \""+key+"\"");
			this.key = key;
		}
		
		public String getKey() {
			return key;
		}
	}
}
