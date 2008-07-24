package com.github.rd4j.expr;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class BaseTokenizer {
	protected final Reader reader;
	protected StringBuilder identity = new StringBuilder();

	protected int curTokenStartLine = 0;
	protected int curTokenStartColumn = 0;
	protected int lastReadLine = 0;
	protected int lastReadColumn = 0;
	protected boolean nextCharIsOnANewLine = false;
	
	boolean isTokenBoundsInvalid = true;
	
	protected void aboutToReturnToken() {
		isTokenBoundsInvalid = true;
	}
	
	public BaseTokenizer(Reader reader) {
		super();
		this.reader = reader;
	}
	
	List<Integer> savedChars = new ArrayList<Integer>();
	
	protected void ungetc(int c) {
		savedChars.add(c);
	}

	protected void advanceLineCounter(int c) {
		if(nextCharIsOnANewLine) {
			lastReadLine ++;
			lastReadColumn = 1;
			
			nextCharIsOnANewLine = false;
		} else {
			lastReadColumn ++;
		}
		
		if(c == '\n') {
			nextCharIsOnANewLine = true;
		}
		
		if(isTokenBoundsInvalid) {
			curTokenStartLine = lastReadLine;
			curTokenStartColumn = lastReadColumn;
		}
	}
	
	protected int getc() {
		int c;
		if(savedChars.size() > 0) {
			c = savedChars.remove(savedChars.size()-1);
		} else {
			try {
				c = reader.read();
				
				// only advance on consuming from raw stream
				// we really should pushback on ungetc but 
				// would make things more complicated and as long
				// as look ahead isn't too deep, this will
				// be a good enough approximation
				advanceLineCounter(c);
				
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
