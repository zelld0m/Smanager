package com.search.manager.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter = BeanConverter.class)
public class RedirectRule implements Serializable {

	private static final long serialVersionUID = 4608433178597830827L;

	private static final String DBL_PIPE_DELIM = "||";
	private static final String OR = ") OR (";
	private static final String COMMA = ",";

	private String ruleId;
	private String ruleName;
	private String redirectTypeId;
	private String storeId;
	private Integer priority;
	private String searchTerm;
	private String condition;
	private String createdBy;
	private String modifiedBy;
	private String changeKeyword;
	private Date dateCreated;
	private Date dateModified;
	
	public RedirectRule() {
		super();
	}

	public RedirectRule(String ruleId, String storeId, String ruleName, String searchTerm) {
		super();
		this.ruleId = ruleId;
		this.ruleName = ruleName;
		this.searchTerm = searchTerm;
	}
	
	public RedirectRule(String storeId, String searchTerm) {
		super();
		this.storeId = storeId;
		this.searchTerm = searchTerm;
	}
	
	public RedirectRule(String ruleId, String redirectTypeId, String ruleName, String storeId,
			Integer priority, String searchTerm, String condition, String createdBy, String modifiedBy, 
			Date dateCreated, Date dateModified, String changeKeyword) {
		super();
		this.ruleId = ruleId;
		this.redirectTypeId = redirectTypeId;
		this.ruleName = ruleName;
		this.storeId = storeId;
		this.priority = priority;
		this.searchTerm = searchTerm;
		this.condition = condition;
		this.createdBy = createdBy;
		this.modifiedBy = modifiedBy;
		this.dateCreated = dateCreated;
		this.dateModified = dateModified;
		this.changeKeyword = changeKeyword;
	}
	
	public String getRuleId() {
		return ruleId;
	}
	
	public void setRuleId(String ruleId) {
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
	
	public String getRedirectTypeId() {
		return redirectTypeId;
	}
	public void setRedirectTypeId(String redirectTypeId) {
		this.redirectTypeId = redirectTypeId;
	}
	
	public boolean isRedirectToPage() {
		return StringUtils.startsWithIgnoreCase(condition, "http://");
	}
	
	public String getRedirectToPage() {
		return StringUtils.trimToEmpty(condition);
	}
	
	public boolean isRedirectFilter() {
		return StringUtils.isEmpty(condition) && !StringUtils.startsWithIgnoreCase(condition, "http://");
	}
	
	/**
	 * Return URL-encoded SOLR fq parameter
	 * @return
	 */
	public String getRedirectFilter() {
		StringBuilder fq = new StringBuilder();
		if (StringUtils.isNotEmpty(condition)) {
			fq = fq.append(condition.replace(DBL_PIPE_DELIM, OR));
		}
		if (fq.length() > 0) {
			fq.insert(0,"(").append(")");
		}
		return fq.toString();
	}
	
	public List<String> getSearchTerms() {
		ArrayList<String> terms = new ArrayList<String>();
		if (StringUtils.isNotEmpty(searchTerm)) {
			CollectionUtils.addAll(terms, searchTerm.split(COMMA));
		}
		return terms;
	}

	public void setChangeKeyword(String changeKeyword) {
		this.changeKeyword = changeKeyword;
	}

	public String getChangeKeyword() {
		return changeKeyword;
	}
}
