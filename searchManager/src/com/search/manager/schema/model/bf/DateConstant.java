package com.search.manager.schema.model.bf;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryParser.ParseException;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.schema.SchemaException;
import com.search.manager.schema.model.GenericType;
import com.search.manager.utility.DateAndTimeUtils;

@DataTransferObject(converter = BeanConverter.class)
public class DateConstant extends Constant {
	
	private static final long serialVersionUID = 1L;
	
	private final static String DATE_NOW = "NOW";
	
	public DateConstant(String value) {
		super(value);
		genericType = GenericType.DATE;
	}
	
	public static boolean isValidConstant(String value) throws SchemaException {
		boolean isValid = false;
		
		if(StringUtils.isNotBlank(value)){
			if(DATE_NOW.equals(value)){
				isValid = true;
			} else
				try {
					if(value.startsWith(DATE_NOW) && DateMathUtil.isValidDateMath(value.substring(DATE_NOW.length()))){
						isValid = true;
					}
				} catch (ParseException e) {
					throw new SchemaException("Invalid Date Math String: " + value + "!");
				}
		}
		
		return isValid;
	}
	
	@Override
	public boolean validate() throws SchemaException {
		boolean valid = StringUtils.isNotEmpty(value);
		valid &= isValidConstant(value);
		if (!valid) {
			if (!DateAndTimeUtils.isValidDateIso8601Format(value)) {
				throw new SchemaException("Invalid Date Format: " + value + "! Date should be either in ISO8601 Canonical Date Format (e.g. 2000-01-01T00:00:00Z) or the value \"NOW\". ");
			}
		}
		return true;
	}

}
