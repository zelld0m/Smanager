package com.search.manager.schema.model.bf;

import com.search.manager.schema.model.GenericType;

public abstract class Constant implements FunctionModelComponent {
	
	private static final long serialVersionUID = 1L;
	
	protected String value;
	protected GenericType genericType;
	
	public Constant(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}

	public GenericType getGenericType() {
		return genericType;
	}
	
	@Override
	public String toString() {
		return value;
	}
}