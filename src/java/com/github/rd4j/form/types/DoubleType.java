package com.github.rd4j.form.types;

import java.util.Map;

public class DoubleType implements BasicType {

	public Object convertFromString(String value) {
		return new Double(Double.parseDouble(value));
	}

	public Class<?> getJavaClass() {
		return Double.class;
	}
}
