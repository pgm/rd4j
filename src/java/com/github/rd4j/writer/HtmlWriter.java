package com.github.rd4j.writer;

public interface HtmlWriter {
	public void writeRaw(String text);
	public void writeEscaped(String text);
}
