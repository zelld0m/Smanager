package com.search.manager.schema.model.mm;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.schema.SchemaException;

@DataTransferObject(converter = BeanConverter.class)
public class FixedMatch implements MinimumToMatch {

	private static final long serialVersionUID = 1L;

	private String match;
	
	public FixedMatch(String match) {
		this.match = match;
	}
	
	public String getMatch() {
		return match;
	}
	
	@Override
	public boolean validate() throws SchemaException {
		if (!(StringUtils.isNotEmpty(match) && Pattern.matches("[+-]?\\d+%??", match))) {
			throw new SchemaException("Invalid value: " + match);
		}
		return true;
	}

	@Override
	public String toString() {
		return StringUtils.trimToEmpty(match);
	}
}
