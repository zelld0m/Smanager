package com.search.manager.schema;

public class SchemaException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public SchemaException(String desc) {
		super(desc);
	}
	
	public SchemaException(String desc, Throwable cause) {
		super(desc, cause);
	}

}
