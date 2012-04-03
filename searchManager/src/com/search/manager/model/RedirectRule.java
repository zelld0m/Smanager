package com.search.manager.model;

import java.io.Serializable;
import java.util.Date;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter = BeanConverter.class)
public class RedirectRule implements Serializable {

	private static final long serialVersionUID = 4608433178597830827L;

	private Integer ruleId;
	private String ruleName;
	private String storeId;
	private Integer priority;
	private String searchTerm;
	private String condition;
	private Integer activeFlag;
	private String createdBy;
	private String modifiedBy;
	private Date dateCreated;
	private Date dateModified;
	
	public RedirectRule() {
		super();
	}
	
	public RedirectRule(Integer ruleId, String ruleName, String storeId,
			Integer priority, String searchTerm, String condition, Integer activeFlag,
			String createdBy, String modifiedBy, Date dateCreated,
			Date dateModified) {
		super();
		this.ruleId = ruleId;
		this.ruleName = ruleName;
		this.storeId = storeId;
		this.priority = priority;
		this.searchTerm = searchTerm;
		this.condition = condition;
		this.activeFlag = activeFlag;
		this.createdBy = createdBy;
		this.modifiedBy = modifiedBy;
		this.dateCreated = dateCreated;
		this.dateModified = dateModified;
	}
	
	public Integer getRuleId() {
		return ruleId;
	}
	public void setRuleId(Integer ruleId) {
		this.ruleId = ruleId;
	}
	public String getRuleName() {
		return ruleName;
	}
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	public String getStoreId() {
		return storeId;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	public String getSearchTerm() {
		return searchTerm;
	}
	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public Integer getActiveFlag() {
		return activeFlag;
	}
	public void setActiveFlag(Integer activeFlag) {
		this.activeFlag = activeFlag;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	public Date getDateModified() {
		return dateModified;
	}
	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}
	
}
