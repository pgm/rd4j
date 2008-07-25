package com.github.rd4j.djangoishtemplate;

public class TemplateParseException extends RuntimeException {
	final String templateName;
	final int lineNumber;
	final int column;
	final String message;
	
	public TemplateParseException(String templateName, int lineNumber, int column, String message) {
		super();
		this.column = column;
		this.lineNumber = lineNumber;
		this.message = message;
		this.templateName = templateName;
	}

	public TemplateParseException(String templateName, int lineNumber, int column, Exception cause) {
		super(cause);
		this.column = column;
		this.lineNumber = lineNumber;
		this.message = cause.getMessage();
		this.templateName = templateName;
	}
	
	public String getTemplateName() {
		return templateName;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public int getColumn() {
		return column;
	}

	@Override
	public String getMessage() {
		return templateName+"("+lineNumber+"): "+message;
	}
}
