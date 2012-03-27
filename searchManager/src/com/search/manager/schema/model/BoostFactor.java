package com.search.manager.schema.model;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.schema.SchemaException;

@DataTransferObject(converter = BeanConverter.class)
public class BoostFactor implements VerifiableModel {

	private static final long serialVersionUID = 1L;

	private String boost;
	
	public BoostFactor(String boost) {
		this.boost = boost;
	}
	
	public String getBoost() {
		return boost;
	}

	public void setBoost(String boost) {
		this.boost = boost;
	}
	
	@Override
	public boolean validate() throws SchemaException {
		// check boost factor
		if (StringUtils.isEmpty(boost)) {
			throw new SchemaException("Empty boost factor.");
		}
		if (!Pattern.compile("\\d+(?:\\.\\d+)?").matcher(boost).matches()) {
			throw new SchemaException("Invalid value for boost factor: " + boost);
		}
		try {
			Float.parseFloat(boost);
		} catch (Exception e) {
			throw new SchemaException("Invalid value for boost factor: " + boost);
		}
		return true;
	}
	
	@Override
	public String toString() {
		return boost;
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof BoostFactor && StringUtils.equals(boost, ((BoostFactor)obj).boost));
	}
	
}
