package com.github.rd4j.form.types;

import java.util.Map;

public class IntegerType implements BasicType {

	public Object convertFromString(String value) {
		return new Integer(Integer.parseInt(value));
	}

	public Class<?> getJavaClass() {
		return Integer.class;
	}
}
