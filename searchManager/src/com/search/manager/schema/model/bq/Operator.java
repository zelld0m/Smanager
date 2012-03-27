package com.search.manager.schema.model.bq;

import java.io.Serializable;

public class Operator implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected boolean withLValue;
	protected boolean withRValue;
	protected String  text;
	protected String  name;
	protected String  regexp;
	protected int     priority;
	
	public Operator(String name, String text, String regexp, boolean withLValue, boolean withRValue, int priority) {
		this.name = name;
		this.text = text;
		this.regexp = regexp;
		this.withLValue = withLValue;
		this.withRValue = withRValue;
		this.priority = priority;
	}

	public boolean isWithLValue() {
		return withLValue;
	}

	public boolean isWithRValue() {
		return withRValue;
	}

	public String getText() {
		return text;
	}

	public String getRegExp() {
		return regexp;
	}
	
	public String getName() {
		return name;
	}
	
	public int getPriority() {
		return priority;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
