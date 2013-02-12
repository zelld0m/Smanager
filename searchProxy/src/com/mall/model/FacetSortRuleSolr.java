package com.mall.model;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.beans.Field;

public class FacetSortRuleSolr {

	@Field("facetSortId")
	private String id;
	@Field
	private String name;
	@Field
	private String ruleType;
	@Field
	private String sortType;
	@Field
	private String store;
	@Field
	private String ruleId;
	@Field
	private String ruleName;
	@Field("*_items")
	private Map<String, List<String>> items;
	@Field("*_groupSortType")
	private Map<String, Integer> groupSortType;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public String getSortType() {
		return sortType;
	}

	public void setSortType(String sortType) {
		this.sortType = sortType;
	}

	public String getStore() {
		return store;
	}

	public void setStore(String store) {
		this.store = store;
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

	public Map<String, List<String>> getItems() {
		return items;
	}

	public void setItems(Map<String, List<String>> items) {
		this.items = items;
	}

	public Map<String, Integer> getGroupSortType() {
		return groupSortType;
	}

	public void setGroupSortType(Map<String, Integer> groupSortType) {
		this.groupSortType = groupSortType;
	}

}
