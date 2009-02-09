package com.github.rd4j.form.types;

import java.util.Map;

public interface ClassType extends Rd4jType {
	public Rd4jType getPropertyType(String childName);
	public Object createNew(Map<String, Object> propertyValues);
}
