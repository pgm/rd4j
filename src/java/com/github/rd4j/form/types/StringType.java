package com.github.rd4j.form.types;

import java.util.Map;

public class StringType implements BasicType {

	public Object convertFromString(String value) {
		return value;
	}

	public Class<?> getJavaClass() {
		return String.class;
	}

}
