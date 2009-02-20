package com.github.rd4j.form;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.rd4j.ErrorCollection;
import com.github.rd4j.expr.AssignmentTree;
import com.github.rd4j.expr.AssignmentTreeOperation;
import com.github.rd4j.expr.ExpressionUtil;
import com.github.rd4j.form.types.BasicType;
import com.github.rd4j.form.types.CustomClassType;
import com.github.rd4j.form.types.IndexedType;
import com.github.rd4j.form.types.Rd4jType;

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
	
/*	
	static public Object populateDefault(TypedReference reference) {
		Rd4jType type = reference.getType();
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
	*/
	

	public static void bind(Map<String,Object> root, Map<String,Rd4jType> types, Map<String,String> bindings, ErrorCollection errorCollection) {
		List<String> keys = new ArrayList<String>();
		keys.addAll(bindings.keySet());

		/*
		// sort by length to ensure that shorter pathes are evaluated first
		Collections.sort(keys, new Comparator<String>() {

			public int compare(String o1, String o2) {
				if(o1.length() == o2.length()) {
					return o1.compareTo(o2);
				}
				
				return o1.length() - o2.length();
			}
		});
		*/

		// construct the tree of expressions.  You could think of it as a prefix tree.  All
		// common paths are merged into a common node.
		AssignmentTree assignmentTree = new AssignmentTree("root");
		for(String key:keys) {
			String valueString = bindings.get(key);
			
			// add this expression to the assignment tree
			// node will point to the terminal leaf of the expression
			AssignmentTree node = ExpressionUtil.parseExpression(assignmentTree, key);

			node.setValue(valueString);
		}

		// now that we have the tree and the typed root, walk down the try, trying to convert each node
		// into a real type.
		for(String key:assignmentTree.getChildrenNames()) {
			Rd4jType type = types.get(key);
			if(type == null)
			{
				throw new RuntimeException("No type bound for parameter "+key);
			}
			AssignmentTree node = assignmentTree.getOrCreateChild(key, AssignmentTreeOperation.DOT);

			// convert tree and children accordingly
			Object object = convertTreeToLocalObject(type, node, errorCollection);
			// and store real object in typed map.  Should get an exception if the 
			// object is of the wrong type.
			root.put(key, object);
		}
		
//		// now that we have a fully typed tree, we can recurse on it depth first translating
//		// 
//			String firstAttribute = ((AttributeExpression)expr).getAttribute();
//			
//			// if we are expecting something with the start of this path, we'll
//			// try to evaluate the expression.   However, if we don't have an
//			// object that starts that way, we silently drop the value.
//			if(root.containsProperty(firstAttribute)) {
//				TypedReference ref = ExpressionUtil.getReferenceFromExpression(root, expr);
//				coerceToType(ref, valueString, errorCollection);
//			} 
//		}
	}
	
	private static Object convertTreeToLocalObject(Rd4jType type, AssignmentTree node, ErrorCollection errorCollection) {
		// base case: basic types
		if(type instanceof BasicType) {
			BasicType basicType = (BasicType)type;
			if(node.getChildrenNames().size() > 0)
				throw new RuntimeException("cannot have children");
			return basicType.convertFromString(node.getValue());
		} else if(type instanceof IndexedType){
			IndexedType indexedType = (IndexedType)type;
			
			// instantiate a new map/list
			Object localObject = indexedType.create();

			// find the converted for the index
			BasicType indexType = indexedType.getIndexType();
			Rd4jType elementType = indexedType.getElementType();

			// go through each child and convert the index from the string(name) and the element(tree)
			for(String childName : node.getChildrenNames())
			{
				Object index = indexType.convertFromString(childName);
				Object element = convertTreeToLocalObject(elementType, node.getChild(childName), errorCollection);
				
				indexedType.set(localObject, index, element);
			}
			
			return localObject;
		} else if(type instanceof CustomClassType) {
			CustomClassType customClassType = (CustomClassType)type;
			
			Map<String, Object> properties = new HashMap<String, Object>();
			
			for(String childName : node.getChildrenNames())
			{
				Rd4jType propertyType = customClassType.getPropertyType(childName);
				if(propertyType == null)
				{
					throw new RuntimeException("No type for "+childName);
				}
				Object element = convertTreeToLocalObject(propertyType, node.getChild(childName), errorCollection);
				properties.put(childName, element);
			}
			
			return customClassType.createNew(properties);
		}
		else
		{
			throw new RuntimeException("unknown type "+type);
		}
	}

}
