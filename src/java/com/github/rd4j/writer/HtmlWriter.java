package com.github.rd4j.writer;

public interface HtmlWriter {
	public void writeRaw(String text);
	public void writeEscaped(String text);

	public void startTag(String tag);
	public void startTagBegin(String tag, boolean hasBody);
	public void tagAttribute(String attribute, String value);
	public void startTagEnd();
	public void endTag();
}
