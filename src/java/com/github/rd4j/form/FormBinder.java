package com.github.rd4j.form;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.rd4j.ErrorCollection;
import com.github.rd4j.TypedMap;
import com.github.rd4j.expr.AttributeExpression;
import com.github.rd4j.expr.Expression;
import com.github.rd4j.expr.ExpressionUtil;
import com.github.rd4j.expr.TypedReference;

public class FormBinder {
	/*
	public static <T extends Form> T bind(Class<T> clazz, Map<String,String> parameters) {
		T boundData;
		
		try {
			boundData = clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		for(Field field : clazz.getFields()) {
			if(Modifier.isPublic(field.getModifiers())) {
				try {
					field.set(boundData, null);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		
		return boundData;
	}
	*/
	
	public static void writeWidget(StringBuilder sb, Class<?> valueType, String name, String fieldName) {
		if(valueType.equals(String.class)) {
			sb.append("<input name=\"");
			sb.append(name);
			sb.append("\" type=\"text\">");
		}
	}
	
	public static void writeAsUl(Class<?> formClass, String formName, StringBuilder sb) {
		sb.append("<ul>\n");
		for(Field field : formClass.getFields()) {
			Widget widget = field.getAnnotation(Widget.class);
	
			if(widget != null) {
				sb.append("<li>");
				sb.append(widget.name());
				writeWidget(sb, field.getType(), field.getName(), formName+"."+widget.name());
				sb.append("</li>\n");
			}
		}
		sb.append("</ul>\n");
	}
	
	
	static public Object populateDefault(TypedReference reference) {
		Type type = reference.getType();
		if(type instanceof Class) {
			Class <?> c = (Class<?>)type;
			Object instance;
			if(c.isAssignableFrom(Map.class)) {
				instance = new HashMap();
			} else if(c.isArray()) {
				instance = Array.newInstance(c.getComponentType(), 0);
			} else if(c.isAssignableFrom(List.class)) {
				instance = new ArrayList();
			} else {
				try {
					instance = c.newInstance();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			reference.set(instance);
			return instance;
		} else {
			throw new RuntimeException("unknown type "+type);
		}
	}
	
	

	public static void bind(TypedMap root, Map<String,String>bindings, ErrorCollection errorCollection) {
		List<String> keys = new ArrayList<String>();
		keys.addAll(bindings.keySet());

		// sort by length to ensure that shorter pathes are evaluated first
		Collections.sort(keys, new Comparator<String>() {

			public int compare(String o1, String o2) {
				if(o1.length() == o2.length()) {
					return o1.compareTo(o2);
				}
				
				return o1.length() - o2.length();
			}
		});
		
		for(String key:keys) {
			String valueString = bindings.get(key);
			
			Expression expr = ExpressionUtil.parseExpression(key);
			String firstAttribute = ((AttributeExpression)expr).getAttribute();
			
			// if we are expecting something with the start of this path, we'll
			// try to evaluate the expression.   However, if we don't have an
			// object that starts that way, we silently drop the value.
			if(root.containsProperty(firstAttribute)) {
				TypedReference ref = ExpressionUtil.getReferenceFromExpression(root, expr);
				coerceToType(ref, valueString, errorCollection);
			} 
		}
	}
	
	public static void coerceToType(TypedReference ref, String valueString, ErrorCollection errorCollection) {
		Type type = ref.getType();
		if(type.equals(Integer.class)) {
			try {
				int value;
				value = Integer.parseInt(valueString);
				ref.set(value);
			} catch (NumberFormatException ex) {
				errorCollection.addError("", "number format");
			}
		} else if(type.equals(Double.class)) {
			try {
				double value;
				value = Double.parseDouble(valueString);
				ref.set(value);
			} catch (NumberFormatException ex) {
				errorCollection.addError("", "double format");
			}
		} else if(type.equals(String.class) || type.equals(Object.class)) {
			ref.set(valueString);
		} else {
			throw new RuntimeException("Could not coerce type "+type);
		}
	}
	
	public static void validateBindings(TypedMap root, ErrorCollection errorCollection) {
		// walk through properties
	}
}
