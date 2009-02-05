package com.github.rd4j.writer;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class StreamHtmlWriter implements HtmlWriter{
	protected final Writer writer;
	
	public StreamHtmlWriter(Writer writer) {
		this.writer = writer;
	}

	public void writeEscaped(String text) {
		// there is probably a better way to do this
		text = text.replace("&", "&amp;");
		text = text.replace("<", "&lt;");
		text = text.replace(">", "&gt;");
		writeRaw(text);
	}

	public void writeRaw(String text) {
		try {
			this.writer.write(text);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	List<String> tagStack = new ArrayList<String>();

	boolean currentTagHasBody;
	
	public void endTag() {
		String currentTag = tagStack.remove(tagStack.size()-1);
		writeRaw("<");
		writeRaw(currentTag);
		writeRaw("/>");
	}

	public void startTag(String tag) {
		tagStack.add(tag);
		writeRaw("<");
		writeRaw(tag);
		writeRaw(">");
	}

	public void startTagBegin(String tag, boolean hasBody) {
		this.currentTagHasBody = hasBody;
		tagStack.add(tag);
		writeRaw("<");
		writeRaw(tag);
	}

	public void startTagEnd() {
		if(currentTagHasBody)
			writeRaw("/");
		writeRaw(">");
	}

	public void tagAttribute(String attribute, String value) {
		writeRaw(" ");
		writeRaw(attribute);
		writeRaw("=");
		writeQuotedString(value);
	}

	private void writeQuotedString(String value) {
		writeRaw("\"");
		writeRaw(value.replace("\"", "\\\""));
		writeRaw("\"");
	}
	
}
