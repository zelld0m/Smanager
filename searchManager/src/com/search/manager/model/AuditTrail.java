package com.search.manager.model;

import java.io.Serializable;
import java.util.Date;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.utility.DateAndTimeUtils;

@DataTransferObject(converter = BeanConverter.class)
public class AuditTrail implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String username;
	private String operation;
	private String entity;
	private String storeId;
	private String keyword;
	private String referenceId;
	private Date   date;
	private String details;
	
	public AuditTrail() {
	}
	
	public AuditTrail(String username, String entity, String operation, String storeId, String keyword,
			String referenceId, Date date, String details) {
		this.username = username;
		this.entity = entity;
		this.operation = operation;
		this.storeId = storeId;
		this.keyword = keyword;
		this.referenceId = referenceId;
		this.date = date;
		this.details = details;
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
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
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
	
	public String getFormatDateTimeUsingConfig(){
		return DateAndTimeUtils.formatDateTimeUsingConfig(getStoreId(), getDate());
	}
	
	public String getElapsedTime(){
		return DateAndTimeUtils.getElapsedTime(getDate(), new Date());
	}
}