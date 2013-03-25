package com.search.manager.model;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;
import org.joda.time.DateTime;

@DataTransferObject(converter = BeanConverter.class)
public class RelevancyField extends ModelBean {

	private static final long serialVersionUID = 1L;

	private Relevancy relevancy;
	private String fieldName;
	private String fieldValue;
	
	public RelevancyField() {
	}

	public RelevancyField(Relevancy relevancy, String fieldName, String fieldValue,
			String createdBy, String lastModifiedBy, DateTime createdDateTime, DateTime lastModifiedDateTime) {
		this.relevancy = relevancy;
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
		this.createdBy = createdBy;
		this.lastModifiedBy = lastModifiedBy;
		this.createdDateTime = createdDateTime;
		this.lastModifiedDateTime = lastModifiedDateTime;
	}
	
	public void setRelevancy(Relevancy relevancy) {
		this.relevancy = relevancy;
	}

	public Relevancy getRelevancy() {
		return relevancy;
	}
	
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	
	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}
	
	public String getFieldValue() {
		return fieldValue;
	}
}