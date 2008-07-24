package com.github.rd4j.expr;

import static com.github.rd4j.expr.ExpressionTokenizer.EOF;
import static com.github.rd4j.expr.ExpressionTokenizer.IDENTIFIER;
import static com.github.rd4j.expr.ExpressionTokenizer.NUMBER;
import static com.github.rd4j.expr.ExpressionTokenizer.STRING;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.github.rd4j.TypedMap;
import com.github.rd4j.form.FormBinder;

public class ExpressionUtil {

	static Expression parseAttrExpression(ExpressionTokenizer tokenizer) {
		int token = tokenizer.getNextToken();
		assert(token == IDENTIFIER);
		String attributeName = tokenizer.getText();

		return new AttributeExpression(attributeName, parseRemainingExpression(tokenizer));
	}
	
	public static Expression parseExpression(String path) {
		ExpressionTokenizer tokenizer = new ExpressionTokenizer(path);
		try { 
			return parseAttrExpression(tokenizer);
		} catch(Exception ex) {
			throw new RuntimeException("Got exception while parsing \""+path+"\"", ex);
		}
	}

	
	public static class UntypedMapReference implements TypedReference {
		final Map<String,Object> map;
		final String name;
		public UntypedMapReference(Map<String,Object> map, String name) {
			this.map = map;
			this.name = name;
		}

		public Object get() {
			if(map.containsKey(name))
				return map.get(name);
			
			throw new RuntimeException("map does not contain "+name);
		}
		
		public Type getType() {
			return Object.class;
		}
		
		public void set(Object obj) {
			map.put(name, obj);
		}
	}
	
	// exp <- var
	// exp <- exp . var
	// exp <- exp[index]
	
	static Expression parseRemainingExpression(ExpressionTokenizer tokenizer) {
		int token = tokenizer.getNextToken();
		if(token == '.') {
			return parseAttrExpression(tokenizer);
		} else if(token == '[') {
			int indexToken = tokenizer.getNextToken();
			assert(indexToken == STRING || indexToken == NUMBER);
			String index = tokenizer.getText();
			token = tokenizer.getNextToken();
			assert(token == ']');

			if(indexToken == NUMBER) {
				return new SubscriptExpression(Integer.parseInt(index), parseRemainingExpression(tokenizer));
			} else {
				throw new RuntimeException("unimp");
			}
		} else if(token == EOF) {
			return null;
		} else {
			throw new RuntimeException("unexpected "+token);
		}
	}

	static class PropertyAccessorReference implements TypedReference {
		final Object target;
		final Method getMethod;
		final Method setMethod;
		
		public PropertyAccessorReference(Object target, Method getMethod, Method setMethod) {
			this.target = target;
			this.setMethod = setMethod;
			this.getMethod = getMethod;
		}

		public Object get() {
			try {
				return this.getMethod.invoke(this.target);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		public Type getType() {
			return this.getMethod.getGenericReturnType();
		}

		public void set(Object obj) {
			try {
				this.setMethod.invoke(this.target, obj);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	static class MemberReference implements TypedReference {
		final Field field;
		final Object target;
		
		public MemberReference(Object target, Field field) {
			this.field = field;
			this.target = target;
		}
		
		public Object get() {
			try {
				return this.field.get(target);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		public Type getType() {
			return this.field.getGenericType();
		}

		public void set(Object obj) {
			try {
				this.field.set(target, obj);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	static public TypedReference getReference(Object target, String name) {
		if(target instanceof TypedMap) {
			TypedMap map = (TypedMap)target;
			return map.getReference(name);
		} if(target instanceof Map) {
			Map<String,Object> map = (Map<String,Object>)target;
			return new UntypedMapReference(map, name);
		} else {
			// first try to find getter 
			Method getMethod = null;
			Method setMethod = null;
			try {
				getMethod = target.getClass().getMethod(constructGetterName(name));
			} catch (SecurityException e1) {
				throw new RuntimeException(e1);
			} catch (NoSuchMethodException e1) {
				// if no method, fallback and try field
			}

			try {
				String setterName = constructSetterName(name);
				for (Method m : target.getClass().getMethods()) {
					if(m.getName().equals(setterName) && m.getParameterTypes().length == 1) {
						setMethod = m;
						break;
					}
				}
			} catch (SecurityException e1) {
				throw new RuntimeException(e1);
			} 

			if(getMethod != null || setMethod != null) {
				return new PropertyAccessorReference(target, getMethod, setMethod);
			}
			
			Field field;
			try {
				field = target.getClass().getField(name);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			return new MemberReference(target, field);
		}
	}

	static public String constructGetterName(String name) {
		return "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	static public String constructSetterName(String name) {
		return "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	
	static class ArrayTypedReference implements TypedReference {
		final Object array;
		final int index;
		
		public ArrayTypedReference(Object array, int index) {
			super();
			this.array = array;
			this.index = index;
		}

		public Object get() {
			return Array.get(array, index);
		}

		public Type getType() {
			return array.getClass().getComponentType();
		}

		public void set(Object value) {
			Array.set(array, index, value);
		}
	}

	static class ListTypedReference implements TypedReference {
		final List list;
		final int index;
		
		public ListTypedReference(List list, int index) {
			super();
			this.list = list;
			this.index = index;
		}

		public Object get() {
			return this.list.get(index);
		}

		public Type getType() {
			throw new RuntimeException("unknown");
		}

		public void set(Object value) {
			this.list.set(index, value);
		}
	}
	
	static class MapTypedReference implements TypedReference {
		final Map map;
		final Object index;
		
		public MapTypedReference(Map map, Object index) {
			super();
			this.map = map;
			this.index = index;
		}

		public Object get() {
			return this.map.get(index);
		}

		public Type getType() {
			throw new RuntimeException("unknown");
		}

		public void set(Object value) {
			this.map.put(index, value);
		}
	}

	
	static public TypedReference getReferenceFromIndex(Object target, Object index) {
		if(target instanceof Map) {
			return new MapTypedReference((Map)target, index);
		} else if (target instanceof List) {
			return new ListTypedReference((List)target, ((Integer)index).intValue());
		} else if (target.getClass().isArray()) {
			return new ArrayTypedReference(target, ((Integer)index).intValue());
		} else {
			throw new RuntimeException("Not an indexed object");
		}
	}

	static public TypedReference getReferenceFromExpression(Object target, Expression expression) {
		TypedReference reference;
		
		do {
			reference = expression.eval(target);
			Expression nextExpression = expression.getNext();
			if(nextExpression != null) {
				Object child = reference.get();
				if(child == null) {
					child = FormBinder.populateDefault(reference);
				}
				target = child;
			}

			expression = nextExpression;
		} while (expression != null);
		
		return reference;
	}
	
}
