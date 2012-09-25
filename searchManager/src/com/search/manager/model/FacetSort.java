package com.search.manager.model;

import java.io.Serializable;
import java.util.List;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.enums.SortType;

@DataTransferObject(converter = BeanConverter.class)
public class FacetSort implements Serializable {

	private static final long serialVersionUID = -489935522624703568L;
	
	private String id;
	private String name;
	private SortType sortType;
	private Store store;
	private List<FacetGroup> facetGroups;
	
	public FacetSort() {}
	
	public FacetSort(String id, String name, SortType sortType,
			List<FacetGroup> facetGroups) {
		super();
		this.id = id;
		this.name = name;
		this.sortType = sortType;
		this.facetGroups = facetGroups;
	}

	public String getId() {
		return id;
	}

	public String getRuleId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setRuleId(String ruleId) {
		this.id = ruleId;
	}

	public String getName() {
		return name;
	}
	
	public String getRuleName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setRuleName(String ruleName) {
		this.name = ruleName;
	}

	public SortType getSortType() {
		return sortType;
	}
	
	public void setSortType(SortType sortType) {
		this.sortType = sortType;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public List<FacetGroup> getFacetGroups() {
		return facetGroups;
	}

	public void setFacetGroups(List<FacetGroup> facetGroups) {
		this.facetGroups = facetGroups;
	}
}