package com.search.manager.schema.model.mm;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.schema.SchemaException;

@DataTransferObject(converter = BeanConverter.class)
public class ConditionalMatch implements MinimumToMatch {

	private static final long serialVersionUID = 1L;

	private String condition;
	private String match;
	
	public ConditionalMatch(String condition, String match) {
		this.setCondition(condition);
		this.match = match;
	}
	
	public ConditionalMatch(String string) {
		String[] params = string.split("<");
		if (params.length > 0) {
			condition = params[0];
			if (params.length > 1) {
				match = params[1];
			}
		}
	}
	
	public String getMatch() {
		return match;
	}

	public void setMatch(String match) {
		this.match = match;
	}


	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getCondition() {
		return condition;
	}

	@Override
	public boolean validate() throws SchemaException {
		if (StringUtils.isEmpty(condition) || StringUtils.isEmpty(match) || !Pattern.matches("\\d+", condition)
				 || !Pattern.matches("[+-]?\\d*%?", match)) {
			throw new SchemaException("Invalid condition: " + this.toString());
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(StringUtils.trimToEmpty(condition)).append("<").append(StringUtils.trimToEmpty(match));
		return builder.toString();
	}
}
