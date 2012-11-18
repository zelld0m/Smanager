package com.search.manager.model;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;
import org.springframework.util.StringUtils;

import com.search.manager.enums.ExportType;
import com.search.manager.enums.RuleStatusEntity;

@DataTransferObject(converter = BeanConverter.class)
public class RuleStatus extends ModelBean {

	private static final long serialVersionUID = 4608433178597830827L;

	private String 		ruleStatusId;
	private Integer 	ruleTypeId;
	private String 		ruleRefId;
	private String 		storeId;
	private String 		description;

	/* Rule Submission / Approval Request */
	private Date 		lastRequestDate;
	private String 		requestBy;
	private String 		updateStatus;
	
	/* Approval */
	private Date 		lastApprovalDate;
	private String 		approvalStatus;
	private String 		approvalBy;
	
	/* Publishing */
	private Date 		lastPublishedDate;
	private String 		publishedBy;
	private String 		publishedStatus;
	
	/* Export */
	private Date 		lastExportDate;
	private String 		exportBy;
	private ExportType	exportType;
	
	private List<Comment> commentList;
	
	public RuleStatus() {
		super();
	}

	public RuleStatus(Integer ruleTypeId, String storeId, String ruleRefId) {
		super();
		this.ruleTypeId = ruleTypeId;
		this.ruleRefId = ruleRefId;
		this.storeId = storeId;
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
		this.createdBy = createdBy;
		this.lastModifiedBy = modifiedBy;
		this.createdDate = dateCreated;
		this.lastModifiedDate = dateModified;
	}

	public RuleStatus(String ruleStatusId, Integer ruleTypeId, String ruleRefId, String storeId, String description,
			String updateStatus, String requestBy, Date lastRequestDate,
			String approvalStatus, String approvalBy, Date lastApprovalDate,
			String publishedStatus, String publishedBy, Date lastPublishedDate,
			ExportType exportType, String exportBy, Date lastExportDate,
			String createdBy, String modifiedBy, Date dateCreated, Date dateModified) {
		this(ruleStatusId, ruleTypeId, ruleRefId, storeId, description, approvalStatus, updateStatus,
				publishedStatus, lastPublishedDate, createdBy, modifiedBy, dateCreated, dateModified);
		this.requestBy = requestBy;
		this.lastRequestDate = lastRequestDate;
		this.approvalBy = approvalBy;
		this.lastApprovalDate = lastApprovalDate;
		this.publishedBy = publishedBy;
		this.exportType = exportType;
		this.exportBy = exportBy;
		this.lastExportDate = lastExportDate;
	}

	public void setRequestApprovalStatus(String requestBy, Date lastRequestDate) {
		this.approvalStatus = RuleStatusEntity.PENDING.toString();
		this.requestBy = requestBy;
		this.lastRequestDate = lastRequestDate;
	}
	
	public void setApprovalStatus(String approvalStatus, String approvalBy, Date lastApprovalDate) {
		this.approvalStatus = approvalStatus;
		this.approvalBy = approvalBy;
		this.lastApprovalDate = lastApprovalDate;
	}

	public void setPubishStatus(String publishStatus, String publishedBy, Date lastPublishedDate) {
		this.publishedStatus = publishStatus;
		this.publishedBy = publishedBy;
		this.lastPublishedDate = lastPublishedDate;
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
		 // TODO: move to constant
		 List<String> locked = Arrays.asList(StringUtils.delimitedListToStringArray("PENDING,APPROVED", ","));
		 return locked.contains(getApprovalStatus());
	}
	
	@Override
	public String toString() {
	    return org.apache.commons.lang.builder.ToStringBuilder.reflectionToString(this);
	}

	public String getRequestBy() {
		return requestBy;
	}

	public void setRequestBy(String requestBy) {
		this.requestBy = requestBy;
	}

	public Date getLastRequestDate() {
		return lastRequestDate;
	}

	public void setLastRequestDate(Date lastRequestDate) {
		this.lastRequestDate = lastRequestDate;
	}

	public String getApprovalBy() {
		return approvalBy;
	}

	public void setApprovalBy(String approvalBy) {
		this.approvalBy = approvalBy;
	}

	public Date getLastApprovalDate() {
		return lastApprovalDate;
	}

	public void setLastApprovalDate(Date lastApprovalDate) {
		this.lastApprovalDate = lastApprovalDate;
	}

	public String getPublishedBy() {
		return publishedBy;
	}

	public void setPublishedBy(String publishedBy) {
		this.publishedBy = publishedBy;
	}

	public Date getLastExportDate() {
		return lastExportDate;
	}

	public void setLastExportDate(Date lastExportDate) {
		this.lastExportDate = lastExportDate;
	}

	public String getExportBy() {
		return exportBy;
	}

	public void setExportBy(String exportBy) {
		this.exportBy = exportBy;
	}

	public ExportType getExportType() {
		return exportType;
	}

	public void setExportType(ExportType exportType) {
		this.exportType = exportType;
	}
	
}
