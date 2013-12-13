package com.search.manager.model;

import java.io.Serializable;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;
import org.joda.time.DateTime;

import com.search.manager.jodatime.jaxbadapter.DateTimeAdapter;

@DataTransferObject(converter = BeanConverter.class)
public class AuditTrail extends ModelBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String username;
	private String operation;
	private String entity;
	private String storeId;
	private String keyword;
	private String referenceId;
	private DateTime createdDate;
	private String details;
	
	public AuditTrail() {
	}
	
	public AuditTrail(String username, String entity, String operation, String storeId, String keyword,
			String referenceId, DateTime dateTime, String details) {
		this.username = username;
		this.entity = entity;
		this.operation = operation;
		this.storeId = storeId;
		this.keyword = keyword;
		this.referenceId = referenceId;
		this.createdDate = dateTime;
		this.details = details;
	}

	public AuditTrail(String referenceId){
		this.referenceId = referenceId;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getOperation() {
		return operation;
	}
	
	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	public String getEntity() {
		return entity;
	}
	
	public void setEntity(String entity) {
		this.entity = entity;
	}
	
	public String getKeyword() {
		return keyword;
	}
	
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	public String getReferenceId() {
		return referenceId;
	}
	
	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}
	
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public DateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(DateTime createdDate) {
		this.createdDate = createdDate;
	}

	public String getDetails() {
		return details;
	}
	
	public void setDetails(String details) {
		this.details = details;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getStoreId() {
		return storeId;
	}
}