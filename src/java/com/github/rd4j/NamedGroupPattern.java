package com.github.rd4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NamedGroupPattern {
	final Pattern simplePattern;
	final Map<String, Integer> posToName = new HashMap<String, Integer>();

	static final Pattern namedGroupPattern = Pattern.compile("\\(\\?P\\<(\\w+)>");

	public NamedGroupPattern(String exp) {
		Matcher m = namedGroupPattern.matcher(exp);
		int nextStart = 0;
		int lastEnd = 0;
		int index = 1;
		
		StringBuilder sb = new StringBuilder();

		while(true) {
			if(!m.find(nextStart))
				break;
			String groupName = m.group(1);
			nextStart = m.end();
			
			posToName.put(groupName, index);
			index++;
			
			sb.append(exp.substring(lastEnd, m.start()));
			sb.append("(");
			lastEnd = m.end();
		}
		sb.append(exp.substring(lastEnd));
		
		simplePattern = Pattern.compile(sb.toString());
	}

	public Matcher matcher(String str) {
		return simplePattern.matcher(str);
	}
	
	public String getNamedGroup(String groupName, Matcher m) {
		if(m.groupCount() != posToName.size()) {
			throw new RuntimeException("named group and unnamed group counts did not match.  (Was there an unnamed group in the original expression?)");
		}
		int index = posToName.get(groupName);
		return m.group(index);
	}
	
	public Map<String, String> getAllGroups(Matcher m) {
		Map<String, String> result = new HashMap<String, String>();
		
		for(Entry<String,Integer>e : posToName.entrySet()) {
			String value = m.group(e.getValue());
			String name = e.getKey();
			
			result.put(name, value);
		}
		
		return result;
	}
}
