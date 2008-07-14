package com.github.rd4j.expr;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class BaseTokenizer {
	protected final Reader reader;
	protected StringBuilder identity = new StringBuilder();

	public BaseTokenizer(Reader reader) {
		super();
		this.reader = reader;
	}
	
	List<Integer> savedChars = new ArrayList<Integer>();
	
	protected void ungetc(int c) {
		savedChars.add(c);
	}
	
	protected int getc() {
		int c;
		if(savedChars.size() > 0) {
			c = savedChars.remove(savedChars.size()-1);
		} else {
			try {
				c = reader.read();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		return c;
	}

	public String getText() {
		return identity.toString();
	}

}
