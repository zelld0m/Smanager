package com.search.manager.schema.model.qf;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.schema.SchemaException;
import com.search.manager.schema.model.BoostFactor;
import com.search.manager.schema.model.Field;

@DataTransferObject(converter = BeanConverter.class)
public class QueryField implements QueryFieldModelComponent {
	
	private static final long serialVersionUID = 1L;
	
	public Field field;
	public BoostFactor boost;
	
	public QueryField(Field field, BoostFactor boost) {
		this.field = field;
		this.boost = boost;
	}
	
	public void setField(Field field) {
		this.field = field;
	}
	
	public Field getField() {
		return field;
	}
	
	public void setBoost(BoostFactor boost) {
		this.boost = boost;
	}
	
	public BoostFactor getBoost() {
		return boost;
	}

	@Override
	public boolean validate() throws SchemaException {
		if (field == null || StringUtils.isEmpty(field.getName())) {
			throw new SchemaException("Missing query field: " + toString());
		}
		boost.validate();
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(field).append("^").append(boost);
		return builder.toString();
	}
	
}
