package com.search.manager.model;

import java.util.Arrays;
import java.util.List;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;
import org.joda.time.DateTime;
import org.springframework.util.StringUtils;

import com.search.manager.enums.ExportType;
import com.search.manager.enums.RuleEntity;
import com.search.manager.enums.RuleStatusEntity;
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.jodatime.JodaPatternType;

@DataTransferObject(converter = BeanConverter.class)
public class RuleStatus extends ModelBean {

	private static final long serialVersionUID = 4608433178597830827L;

	private String 		ruleStatusId;
	private Integer 	ruleTypeId;
	private String 		ruleRefId;
	private String 		storeId;
	private String 		description;

	/* Rule Submission / Approval Request */
	private DateTime 	lastRequestDateTime;
	private String 		requestBy;
	private String 		updateStatus;

	/* Approval */
	private DateTime 	lastApprovalDateTime;
	private String 		approvalStatus;
	private String 		approvalBy;

	/* Publishing */
	private DateTime 	lastPublishedDateTime;
	private String 		publishedBy;
	private String 		publishedStatus;

	/* Export */
	private DateTime 	lastExportDateTime;
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
			String publishedStatus, DateTime lastPublishedDateTime, String createdBy, String modifiedBy, DateTime createdDateTime, DateTime modifiedDateTime) {
		super();
		this.ruleStatusId = ruleStatusId;
		this.ruleTypeId = ruleTypeId;
		this.ruleRefId = ruleRefId;
		this.storeId = storeId;
		this.description = description;
		this.approvalStatus = approvalStatus;
		this.updateStatus = updateStatus;
		this.publishedStatus = publishedStatus;
		this.lastPublishedDateTime = lastPublishedDateTime;
		this.createdBy = createdBy;
		this.lastModifiedBy = modifiedBy;
		this.createdDate = createdDateTime;
		this.lastModifiedDate = modifiedDateTime;
	}

	public RuleStatus(RuleEntity ruleEntity, String storeId, String ruleId, String ruleName, String createdBy, String modifiedBy, 
			RuleStatusEntity ruleStatus, RuleStatusEntity publishedStatus) {
		this.createdBy = createdBy;
		this.lastModifiedBy = modifiedBy;
		if (publishedStatus!= null) {
			this.publishedStatus = String.valueOf(publishedStatus);
		}
		this.storeId = storeId;
		if (ruleEntity != null) {
			this.ruleTypeId = ruleEntity.getCode();
		}
		this.ruleRefId = ruleId;
		this.description = ruleName;
		if (ruleStatus != null) {
			this.updateStatus = String.valueOf(ruleStatus);
		}
	}

	public RuleStatus(String ruleStatusId, Integer ruleTypeId, String ruleRefId, String storeId, String description,
			String updateStatus, String requestBy, DateTime lastRequestDateTime,
			String approvalStatus, String approvalBy, DateTime lastApprovalDateTime,
			String publishedStatus, String publishedBy, DateTime lastPublishedDateTime,
			ExportType exportType, String exportBy, DateTime lastExportDateTime,
			String createdBy, String modifiedBy, DateTime dateCreated, DateTime dateModified) {
		this(ruleStatusId, ruleTypeId, ruleRefId, storeId, description, approvalStatus, updateStatus,
				publishedStatus, lastPublishedDateTime, createdBy, modifiedBy, dateCreated, dateModified);
		this.requestBy = requestBy;
		this.lastRequestDateTime = lastRequestDateTime;
		this.approvalBy = approvalBy;
		this.lastApprovalDateTime = lastApprovalDateTime;
		this.publishedBy = publishedBy;
		this.exportType = exportType;
		this.exportBy = exportBy;
		this.lastExportDateTime = lastExportDateTime;
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

	public String getApprovalBy() {
		return approvalBy;
	}

	public void setApprovalBy(String approvalBy) {
		this.approvalBy = approvalBy;
	}

	public String getPublishedBy() {
		return publishedBy;
	}

	public void setPublishedBy(String publishedBy) {
		this.publishedBy = publishedBy;
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

	public String getRuleId(){
		return this.ruleRefId;
	}

	public String getRuleName(){
		return this.description;
	}

	public DateTime getLastRequestDateTime() {
		return lastRequestDateTime;
	}

	public void setLastRequestDateTime(DateTime lastRequestDateTime) {
		this.lastRequestDateTime = lastRequestDateTime;
	}

	public DateTime getLastApprovalDateTime() {
		return lastApprovalDateTime;
	}

	public void setLastApprovalDateTime(DateTime lastApprovalDateTime) {
		this.lastApprovalDateTime = lastApprovalDateTime;
	}

	public DateTime getLastPublishedDateTime() {
		return lastPublishedDateTime;
	}

	public void setLastPublishedDateTime(DateTime lastPublishedDateTime) {
		this.lastPublishedDateTime = lastPublishedDateTime;
	}

	public DateTime getLastExportDateTime() {
		return lastExportDateTime;
	}

	public void setLastExportDateTime(DateTime lastExportDateTime) {
		this.lastExportDateTime = lastExportDateTime;
	}
	
	public String getFormattedLastRequestDateTime() {
		return JodaDateTimeUtil.formatFromStorePattern(getLastRequestDateTime(), JodaPatternType.DATE_TIME);
	}
	
	public String getFormattedLastApprovalDateTime() {
		return JodaDateTimeUtil.formatFromStorePattern(getLastApprovalDateTime(), JodaPatternType.DATE_TIME);
	}
	
	public String getFormattedLastPublishedDateTime() {
		return JodaDateTimeUtil.formatFromStorePattern(getLastPublishedDateTime(), JodaPatternType.DATE_TIME);
	}
	
	public String getFormattedLastExportDateTime() {
		return JodaDateTimeUtil.formatFromStorePattern(getLastExportDateTime(), JodaPatternType.DATE_TIME);
	}
}