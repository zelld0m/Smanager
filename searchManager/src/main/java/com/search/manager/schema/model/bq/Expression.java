package com.search.manager.schema.model.bq;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.schema.SchemaException;
import com.search.manager.schema.model.VerifiableModel;

@DataTransferObject(converter = BeanConverter.class)
public class Expression<X,Y> implements VerifiableModel {
	
	private static final long serialVersionUID = 1L;
	
	private X lValue;
	private Y rValue;
	private Operator operator;
	
	public Expression(Operator operator, X lValue, Y rValue) {
		this.operator = operator;
		this.lValue = lValue;
		this.rValue = rValue;
	}

	public Expression(Operator operator, X lValue) {
		this.operator = operator;
		this.lValue = lValue;
	}
	
	public Expression(Operator operator) {
		this.operator = operator;
	}
	
	public Expression() {
	}
	
	public void setOperator(Operator operator) {
		this.operator = operator;
	}
	
	public void setLValue(X lValue) {
		this.lValue = lValue;
	}

	public void setRValue(Y rValue) {
		this.rValue = rValue;
	}
	
	public X getLValue() {
		return lValue;
	}

	public Y getRValue() {
		return rValue;
	}

	public Operator getOperator() {
		return operator;
	}
	
	private final static String LVALUE = "%lvalue%";
	private final static String RVALUE = "%rvalue%";
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (operator != null) {
			builder.append(operator.getText());
			int index = builder.indexOf(LVALUE);
			if (index >= 0) {
				builder.replace(index, index + LVALUE.length(),
						   (lValue == null) ? "" : StringUtils.trimToEmpty(lValue.toString()));
			}
			index = builder.indexOf(RVALUE);
			if (index >= 0) {
				builder.replace(builder.indexOf(RVALUE), index + RVALUE.length(),
						   (rValue == null) ? "" : StringUtils.trimToEmpty(rValue.toString()));
			}
		}
		return builder.toString();
	}

	@Override
	public boolean validate() throws SchemaException {
		boolean isFilterOperator = false;
		
		if (operator == null) {
			throw new SchemaException("No operator defined!");
		}
		
		isFilterOperator = operator instanceof FilterOperator;
		
		if (operator.isWithLValue()) {
			if (lValue == null || StringUtils.isEmpty(lValue.toString())) {
				throw new SchemaException("No lvalue defined!");
			}
			if (isFilterOperator && !(lValue instanceof String)) {
				throw new SchemaException("Expected string. Got expression: " + lValue.toString());
			}
		}
		if (operator.isWithRValue()) {
			if (rValue == null || StringUtils.isEmpty(rValue.toString())) {
				throw new SchemaException("No rValue defined!");
			}
			if (isFilterOperator && !(rValue instanceof String)) {
				throw new SchemaException("Expected string. Got expression: " + lValue.toString());
			}
		}
		return true;
	}
	
}
