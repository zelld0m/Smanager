package com.search.manager.schema.model.bf;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.schema.SchemaException;
import com.search.manager.schema.model.GenericType;

@DataTransferObject(converter = BeanConverter.class)
public class NumericConstant extends Constant {

	private static final long serialVersionUID = 1L;

	public NumericConstant(String value) {
		super(value);
		genericType = GenericType.NUMERIC;
	}

	@Override
	public boolean validate() throws SchemaException {
		boolean valid = StringUtils.isNotEmpty(value);
		if (valid) {
			try {
				Double.parseDouble(value);
				return true;
			} catch (NumberFormatException e) {
			}
		}
		throw new SchemaException("Not a valid float value: " + value + "!");
	}
	
}
