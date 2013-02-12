package com.mall.model;

import org.apache.solr.client.solrj.beans.Field;

public class RuleSolrResult {

	@Field
	private String id;
	@Field
	private Integer location;
	@Field
	private boolean forceAdd;
	@Field
	private String store;
	@Field
	private String keyword;
	@Field
	private String expiryDate;
	@Field
	private String entity;
	@Field
	private String value;
	@Field
	private String memberId;
	@Field
	private String rule;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getLocation() {
		return location;
	}

	public void setLocation(Integer location) {
		this.location = location;
	}

	public boolean getForceAdd() {
		return forceAdd;
	}

	public void setForceAdd(boolean forceAdd) {
		this.forceAdd = forceAdd;
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

	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

}
