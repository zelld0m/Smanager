package com.search.manager.solr.model;

import org.apache.solr.client.solrj.beans.Field;

public class TypeaheadRuleSolr {

	@Field
	private String ruleId;
	@Field
	private String store;
	@Field
	private String keyword;
	@Field
	private Integer priority;
	@Field
	private Boolean disabled;

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getStore() {
		return store;
	}

	public void setStore(String store) {
		this.store = store;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Boolean getDisabled() {
		return disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

}
