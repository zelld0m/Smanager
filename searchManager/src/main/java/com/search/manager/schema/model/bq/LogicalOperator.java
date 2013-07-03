package com.search.manager.schema.model.bq;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter = BeanConverter.class)
public class LogicalOperator extends Operator {

	private static final long serialVersionUID = 1L;
	
	public LogicalOperator(String name, String text, String regexp, boolean withLValue, boolean withRValue, int priority) {
		super(name, text, regexp, withLValue, withRValue, priority);
	}
	
}
