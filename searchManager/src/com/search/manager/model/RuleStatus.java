package com.search.manager.model;

import java.io.Serializable;
import java.util.Date;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter = BeanConverter.class)
public class RuleStatus extends ModelBean implements Serializable {

	private static final long serialVersionUID = 4608433178597830827L;

	private String ruleStatusId;
	private String entity;
	private String ruleRefId;
	private String description;
	private String approvalStatus;
	private String updateStatus;
	private String publishedStatus;
	private Date lastPublishedDate;
	private String comment;
	
	public RuleStatus() {
		super();
	}

	public RuleStatus(String ruleStatusId, String entity, String ruleRefId,
			String description, String approvalStatus, String updateStatus,
			String publishedStatus, Date lastPublishedDate, String comment,
			String createdBy, String modifiedBy, Date dateCreated,
			Date dateModified) {
		super();
		this.ruleStatusId = ruleStatusId;
		this.entity = entity;
		this.ruleRefId = ruleRefId;
		this.description = description;
		this.approvalStatus = approvalStatus;
		this.updateStatus = updateStatus;
		this.publishedStatus = publishedStatus;
		this.lastPublishedDate = lastPublishedDate;
		this.comment = comment;
		setCreatedBy(createdBy);
		setLastModifiedBy(modifiedBy);
		setCreatedDate(dateCreated);
		setLastModifiedDate(dateModified);
	}

	public String getRuleStatusId() {
		return ruleStatusId;
	}

	public void setRuleStatusId(String ruleStatusId) {
		this.ruleStatusId = ruleStatusId;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getRuleRefId() {
		return ruleRefId;
	}

	public void setRuleRefId(String ruleRefId) {
		this.ruleRefId = ruleRefId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(String approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	public String getUpdateStatus() {
		return updateStatus;
	}

	public void setUpdateStatus(String updateStatus) {
		this.updateStatus = updateStatus;
	}

	public Date getLastPublishedDate() {
		return lastPublishedDate;
	}

	public void setLastPublishedDate(Date lastPublishedDate) {
		this.lastPublishedDate = lastPublishedDate;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getPublishedStatus() {
		return publishedStatus;
	}

	public void setPublishedStatus(String publishedStatus) {
		this.publishedStatus = publishedStatus;
	}
	
}
