package com.search.manager.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;
import org.springframework.util.StringUtils;

@DataTransferObject(converter = BeanConverter.class)
public class RuleStatus extends ModelBean implements Serializable {

	private static final long serialVersionUID = 4608433178597830827L;

	private String ruleStatusId;
	private Integer ruleTypeId;
	private String ruleRefId;
	private String storeId;
	private String description;
	private String approvalStatus;
	private String updateStatus;
	private String publishedStatus;
	private Date lastPublishedDate;
	private List<Comment> commentList;
	
	public RuleStatus() {
		super();
	}

	public RuleStatus(String ruleStatusId, Integer ruleTypeId, String ruleRefId, String storeId, String description, String approvalStatus, String updateStatus,
			String publishedStatus, Date lastPublishedDate, String createdBy, String modifiedBy, Date dateCreated, Date dateModified) {
		super();
		this.ruleStatusId = ruleStatusId;
		this.ruleTypeId = ruleTypeId;
		this.ruleRefId = ruleRefId;
		this.storeId = storeId;
		this.description = description;
		this.approvalStatus = approvalStatus;
		this.updateStatus = updateStatus;
		this.publishedStatus = publishedStatus;
		this.lastPublishedDate = lastPublishedDate;
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

	public String getRuleRefId() {
		return ruleRefId;
	}

	public void setRuleRefId(String ruleRefId) {
		this.ruleRefId = ruleRefId;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
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

	public Integer getRuleTypeId() {
		return ruleTypeId;
	}

	public void setRuleTypeId(Integer ruleTypeId) {
		this.ruleTypeId = ruleTypeId;
	}

	public List<Comment> getCommentList() {
		return commentList;
	}

	public void setCommentList(List<Comment> commentList) {
		this.commentList = commentList;
	}
	
	public boolean isLocked(){
		 List<String> locked = Arrays.asList(StringUtils.delimitedListToStringArray("PENDING,APPROVED", ","));
		 return locked.contains(getApprovalStatus());
	}
	
	@Override
	public String toString() {
	    return org.apache.commons.lang.builder.ToStringBuilder.reflectionToString(this);
	}
}
