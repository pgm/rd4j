package com.github.rd4j.form.types;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class Types {
	public static final IntegerType INTEGER = new IntegerType();
	public static final StringType STRING = new StringType();
	public static final DoubleType DOUBLE = new DoubleType();
	
	public static Rd4jType coerceToRd4jType(Type type) {
		if(type instanceof Class<?>)
		{
			Class<?> clazz = (Class<?>)type;
			
			if(clazz.isAssignableFrom(String.class))
				return STRING;
			else if(clazz.isAssignableFrom(Double.class))
				return DOUBLE;
			else if(clazz.isAssignableFrom(Integer.class))
				return INTEGER;
			else if(clazz.isAssignableFrom(Map.class))
			{
				throw new RuntimeException("Could not coerce "+type);
			}
			else if(clazz.isAssignableFrom(List.class))
			{
				throw new RuntimeException("Could not coerce "+type);
			}
			else 
				return new CustomClassType(clazz);
		}
		else if(type instanceof ParameterizedType)
		{
			ParameterizedType parameterizedType =(ParameterizedType) type;
			Type []args = parameterizedType.getActualTypeArguments();
			Class<?> rawType = (Class<?>)parameterizedType.getRawType();
			
			if(rawType.isAssignableFrom(Map.class))
			{
				return new MapType((BasicType)coerceToRd4jType(args[0]), coerceToRd4jType(args[1]));
			}
			else if(rawType.isAssignableFrom(List.class))
			{
				return new ListType(coerceToRd4jType(args[0]));
			}
			else
			{
				throw new RuntimeException("could not handle parameterized type with rawType "+rawType);
			}
		}
		else
		{
			throw new RuntimeException("Could not handle type "+type);
		}
	}
}
