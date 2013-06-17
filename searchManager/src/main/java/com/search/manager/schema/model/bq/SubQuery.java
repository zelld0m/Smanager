package com.search.manager.schema.model.bq;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.schema.SchemaException;
import com.search.manager.schema.model.Field;
import com.search.manager.schema.model.VerifiableModel;

@DataTransferObject(converter = BeanConverter.class)
public class SubQuery implements VerifiableModel {

	private static final long serialVersionUID = 1L;

	private Field field;
	private Expression expression;
	
	public SubQuery() {
	}
	
	public SubQuery(Field field, Expression expression) {
		this.field = field;
		this.expression = expression;
	}
	
	@Override
	public boolean validate() throws SchemaException {
		// TODO Auto-generated method stub
		return false;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public Field getField() {
		return field;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}

	public Expression getExpression() {
		return expression;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(field.getName()).append(":").append(expression.toString());
		return builder.toString();
	}
	
}
