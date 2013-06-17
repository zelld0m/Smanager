package com.search.manager.solr.model;

import org.apache.solr.client.solrj.beans.Field;

public class SpellRuleSolr {

	@Field
	private String ruleId;
	@Field
	private String store;
	@Field
	private String[] keywords;
	@Field
	private String[] suggests;

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

	public String[] getKeywords() {
		return keywords;
	}

	public void setKeywords(String[] keywords) {
		this.keywords = keywords;
	}

	public String[] getSuggests() {
		return suggests;
	}

	public void setSuggests(String[] suggests) {
		this.suggests = suggests;
	}

}