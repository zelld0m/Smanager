package com.search.manager.model;

import java.io.Serializable;
import java.util.Date;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.service.UtilityService;
import com.search.manager.utility.DateAndTimeUtils;

@DataTransferObject(converter = BeanConverter.class)
public class Comment implements Serializable {
	
private static final long serialVersionUID = 1L;
	
	private String username;
	private String date;
	private String comment;
	private Integer commentId;
	private String referenceId;
	private Date createdDate;
	private Integer ruleTypeId;
	
	public Comment() {
	}

	public Comment(Integer commentId, String referenceId, String comment, String username, Date createdDate, Integer ruleTypeId) {
		super();
		this.commentId = commentId;
		this.referenceId = referenceId;
		this.comment = comment;
		this.username = username;
		this.createdDate = createdDate;
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

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Integer getRuleTypeId() {
		return ruleTypeId;
	}

	public void setRuleTypeId(Integer ruleTypeId) {
		this.ruleTypeId = ruleTypeId;
	}

	public String getFormatDateTimeUsingConfig(){
		//TODO: fix call to getStoreId()
		return DateAndTimeUtils.formatDateTimeUsingConfig(UtilityService.getStoreName(), createdDate);
	}
}
