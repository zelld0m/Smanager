package com.search.manager.core.model;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;
import org.joda.time.DateTime;
import org.springframework.util.StringUtils;

import com.search.manager.enums.ExportType;
import com.search.manager.enums.RuleEntity;
import com.search.manager.enums.RuleStatusEntity;
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.jodatime.JodaPatternType;
import com.search.manager.jodatime.jaxbadapter.DateTimeAdapter;

@DataTransferObject(converter = BeanConverter.class)
public class RuleStatus extends ModelBean {

	private static final long serialVersionUID = 4608433178597830827L;

	private String ruleStatusId;
	private Integer ruleTypeId;
	private String ruleRefId;
	private String storeId;
	private String description;

	/* Rule Submission / Approval Request */
	private DateTime lastRequestDate;
	private String requestBy;
	private String updateStatus;

	/* Approval */
	private DateTime lastApprovalDate;
	private String approvalStatus;
	private String approvalBy;

	/* Publishing */
	private DateTime lastPublishedDate;
	private String publishedBy;
	private String publishedStatus;

	/* Export */
	private DateTime lastExportDate;
	private String exportBy;
	private ExportType exportType;

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

	public RuleStatus(String ruleStatusId, Integer ruleTypeId,
			String ruleRefId, String storeId, String description,
			String approvalStatus, String updateStatus, String publishedStatus,
			DateTime lastPublishedDateTime, String createdBy,
			String modifiedBy, DateTime createdDateTime,
			DateTime modifiedDateTime) {
		super();
		this.ruleStatusId = ruleStatusId;
		this.ruleTypeId = ruleTypeId;
		this.ruleRefId = ruleRefId;
		this.storeId = storeId;
		this.description = description;
		this.approvalStatus = approvalStatus;
		this.updateStatus = updateStatus;
		this.publishedStatus = publishedStatus;
		this.lastPublishedDate = lastPublishedDateTime;
		this.createdBy = createdBy;
		this.lastModifiedBy = modifiedBy;
		this.createdDate = createdDateTime;
		this.lastModifiedDate = modifiedDateTime;
	}

	public RuleStatus(RuleEntity ruleEntity, String storeId, String ruleId,
			String ruleName, String createdBy, String modifiedBy,
			RuleStatusEntity ruleStatus, RuleStatusEntity publishedStatus) {
		this.createdBy = createdBy;
		this.lastModifiedBy = modifiedBy;
		if (publishedStatus != null) {
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

	public RuleStatus(String ruleStatusId, Integer ruleTypeId,
			String ruleRefId, String storeId, String description,
			String updateStatus, String requestBy,
			DateTime lastRequestDateTime, String approvalStatus,
			String approvalBy, DateTime lastApprovalDateTime,
			String publishedStatus, String publishedBy,
			DateTime lastPublishedDateTime, ExportType exportType,
			String exportBy, DateTime lastExportDateTime, String createdBy,
			String modifiedBy, DateTime dateCreated, DateTime dateModified) {

		this(ruleStatusId, ruleTypeId, ruleRefId, storeId, description,
				approvalStatus, updateStatus, publishedStatus,
				lastPublishedDateTime, createdBy, modifiedBy, dateCreated,
				dateModified);
		this.requestBy = requestBy;
		this.lastRequestDate = lastRequestDateTime;
		this.approvalBy = approvalBy;
		this.lastApprovalDate = lastApprovalDateTime;
		this.publishedBy = publishedBy;
		this.exportType = exportType;
		this.exportBy = exportBy;
		this.lastExportDate = lastExportDateTime;
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

	public boolean isLocked() {
		// TODO: move to constant
		List<String> locked = Arrays.asList(StringUtils
				.delimitedListToStringArray("PENDING,APPROVED", ","));
		return locked.contains(getApprovalStatus());
	}

	@Override
	public String toString() {
		return org.apache.commons.lang.builder.ToStringBuilder
				.reflectionToString(this);
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

	public String getRuleId() {
		return this.ruleRefId;
	}

	public String getRuleName() {
		return this.description;
	}

	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public DateTime getLastRequestDate() {
		return lastRequestDate;
	}

	public void setLastRequestDate(DateTime lastRequestDate) {
		this.lastRequestDate = lastRequestDate;
	}

	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public DateTime getLastApprovalDate() {
		return lastApprovalDate;
	}

	public void setLastApprovalDate(DateTime lastApprovalDate) {
		this.lastApprovalDate = lastApprovalDate;
	}

	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public DateTime getLastPublishedDate() {
		return lastPublishedDate;
	}

	public void setLastPublishedDate(DateTime lastPublishedDate) {
		this.lastPublishedDate = lastPublishedDate;
	}

	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public DateTime getLastExportDate() {
		return lastExportDate;
	}

	public void setLastExportDate(DateTime lastExportDate) {
		this.lastExportDate = lastExportDate;
	}

	public String getFormattedLastRequestDateTime() {
		return JodaDateTimeUtil.formatFromStorePattern(getLastRequestDate(),
				JodaPatternType.DATE_TIME);
	}

	public String getFormattedLastApprovalDateTime() {
		return JodaDateTimeUtil.formatFromStorePattern(getLastApprovalDate(),
				JodaPatternType.DATE_TIME);
	}

	public String getFormattedLastPublishedDateTime() {
		return JodaDateTimeUtil.formatFromStorePattern(getLastPublishedDate(),
				JodaPatternType.DATE_TIME);
	}

	public String getFormattedLastExportDateTime() {
		return JodaDateTimeUtil.formatFromStorePattern(getLastExportDate(),
				JodaPatternType.DATE_TIME);
	}

	public String getFormattedLastRequestDate() {
		return JodaDateTimeUtil.formatFromStorePattern(getLastRequestDate(),
				JodaPatternType.DATE);
	}

	public String getFormattedLastApprovalDate() {
		return JodaDateTimeUtil.formatFromStorePattern(getLastApprovalDate(),
				JodaPatternType.DATE);
	}

	public String getFormattedLastPublishedDate() {
		return JodaDateTimeUtil.formatFromStorePattern(getLastPublishedDate(),
				JodaPatternType.DATE);
	}

	public String getFormattedLastExportDate() {
		return JodaDateTimeUtil.formatFromStorePattern(getLastExportDate(),
				JodaPatternType.DATE);
	}
}