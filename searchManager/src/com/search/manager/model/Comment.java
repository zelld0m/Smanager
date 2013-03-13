package com.search.manager.model;

import java.io.Serializable;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;
import org.joda.time.DateTime;

@DataTransferObject(converter = BeanConverter.class)
public class Comment implements Serializable {
	
private static final long serialVersionUID = 1L;
	
	private String username;
	private String date;
	private String comment;
	private Integer commentId;
	private String referenceId;
	private DateTime createdDateTime;
	private Integer ruleTypeId;
	private Store store;
	
	public Comment() {}

	public Comment(Integer commentId, String referenceId, String comment, String username, DateTime createdDateTime, Integer ruleTypeId) {
		super();
		this.commentId = commentId;
		this.referenceId = referenceId;
		this.comment = comment;
		this.username = username;
		this.createdDateTime = createdDateTime;
		this.ruleTypeId = ruleTypeId;
	}
	
	public Comment(Store store, String referenceId, Integer ruleTypeId, String comment, String username){
		super();
		this.store = store;
		this.referenceId = referenceId;
		this.ruleTypeId = ruleTypeId;
		this.comment = comment;
		this.username = username;
	}
	
	public Comment(Store store, String referenceId, Integer ruleTypeId){
		super();
		this.store = store;
		this.referenceId = referenceId;
		this.ruleTypeId = ruleTypeId;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Integer getCommentId() {
		return commentId;
	}

	public void setCommentId(Integer commentId) {
		this.commentId = commentId;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getUsername() {
		return username;
	}
	
	public DateTime getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(DateTime createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public Integer getRuleTypeId() {
		return ruleTypeId;
	}

	public void setRuleTypeId(Integer ruleTypeId) {
		this.ruleTypeId = ruleTypeId;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	//TODO: tlds or JodaTimeUtil
//	public String getFormatDateTimeUsingConfig(){
//		//TODO: fix call to getStoreId()
//		// currently only used in GUI so no issue
//		return DateAndTimeUtils.formatDateTimeUsingConfig(UtilityService.getStoreId(), createdDateTime);
//	}
}
