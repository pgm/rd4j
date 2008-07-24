package com.github.rd4j.writer;

import java.io.IOException;
import java.io.Writer;

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
}
