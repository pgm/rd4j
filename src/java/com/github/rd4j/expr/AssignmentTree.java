package com.github.rd4j.expr;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AssignmentTree {
	final String name;
	final Map<String, AssignmentTree> children;

	String value;
	AssignmentTreeOperation childOperation;

	public AssignmentTree(String name) {
		super();
		this.name = name;
		this.children = new HashMap<String, AssignmentTree>();
	}
	
	public AssignmentTree getOrCreateChild(String childName, AssignmentTreeOperation op)
	{
		if(childOperation == null) {
			childOperation = op;
		} else if(childOperation != op) {
			throw new RuntimeException("inconsistent child operations: "+op+" != "+childOperation);
		}
		
		if(children.containsKey(childName))
		{
			return children.get(childName);
		}
		
		// otherwise it doesn't exist, so create it
		AssignmentTree node = new AssignmentTree(childName);
		children.put(childName, node);

		return node;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isLeaf() {
		return children.size()==0;
	}

	public Collection<String> getChildrenNames() {
		return children.keySet();
	}

	public AssignmentTree getChild(String childName) {
		return children.get(childName);
	}
}
