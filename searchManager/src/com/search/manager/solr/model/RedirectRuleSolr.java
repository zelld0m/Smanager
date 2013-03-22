package com.search.manager.solr.model;

import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

public class RedirectRuleSolr {

	@Field
	private String ruleId;
	@Field
	private String ruleName;
	@Field
	private String redirectType;
	@Field
	private String storeId;
	@Field
	private Integer priority;
	@Field
	private List<String> searchTerms;
	@Field
	private String searchTerm;
	@Field
	private String condition;
	@Field
	private String changeKeyword;
	@Field
	private Boolean includeKeyword;
	@Field
	private String redirectUrl;
	@Field
	private Integer messageType;
	@Field
	private String customText;

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

	public String getRedirectType() {
		return redirectType;
	}

	public void setRedirectType(String redirectType) {
		this.redirectType = redirectType;
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

	public List<String> getSearchTerms() {
		return searchTerms;
	}

	public void setSearchTerms(List<String> searchTerms) {
		this.searchTerms = searchTerms;
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

	public String getChangeKeyword() {
		return changeKeyword;
	}

	public void setChangeKeyword(String changeKeyword) {
		this.changeKeyword = changeKeyword;
	}

	public Boolean getIncludeKeyword() {
		return includeKeyword;
	}

	public void setIncludeKeyword(Boolean includeKeyword) {
		this.includeKeyword = includeKeyword;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public Integer getMessageType() {
		return messageType;
	}

	public void setMessageType(Integer messageType) {
		this.messageType = messageType;
	}

	public String getCustomText() {
		return customText;
	}

	public void setCustomText(String customText) {
		this.customText = customText;
	}
}
