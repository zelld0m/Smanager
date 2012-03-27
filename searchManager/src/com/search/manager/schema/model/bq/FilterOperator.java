package com.search.manager.schema.model.bq;

public class FilterOperator extends LogicalOperator {

	private static final long serialVersionUID = 1L;

	public FilterOperator(String name, String text, String regexp, boolean withLValue, boolean withRValue, int priority) {
		super(name, text, regexp, withLValue, withRValue, priority);
	}
	
}
