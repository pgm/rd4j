package com.github.rd4j.form.types;

public interface IndexedType extends Rd4jType {
	public Rd4jType getElementType();
	public BasicType getIndexType();
	public Object create();
	public void set(Object collection, Object index, Object value);
}
