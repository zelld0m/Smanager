package com.search.manager.model;

import java.util.List;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.enums.RuleType;
import com.search.manager.enums.SortType;
import com.search.manager.utility.DateAndTimeUtils;

@DataTransferObject(converter = BeanConverter.class)
public class FacetSort extends ModelBean{

	private static final long serialVersionUID = -489935522624703568L;
	
	private String id;
	private String name;
	private RuleType ruleType;
	private SortType sortType;
	private Store store;
	private List<FacetGroup> facetGroups;
	
	public FacetSort() {}
	
	public FacetSort(String id, String name, RuleType ruleType, 
			SortType sortType, Store store, List<FacetGroup> facetGroups) {
		super();
		this.id = id;
		this.name = name;
		this.sortType = sortType;
		this.ruleType = ruleType;
		this.store = store;
		this.facetGroups = facetGroups;
	}
	
	public FacetSort(String id, String name, RuleType ruleType,
			SortType sortType, Store store) {
		this(id,name, ruleType, sortType, store, null);
	}
	
	public FacetSort(String name, RuleType ruleType,
			SortType sortType, Store store) {
		this("", name, ruleType, sortType, store);
	}
	
	public FacetSort(String name, String ruleType, 
			String sortType, String store) {
		this(name, RuleType.get(ruleType), SortType.get(sortType), new Store(store));
	}
	
	public FacetSort(String id, String name, String store) {
		super();
		this.id = id;
		this.name = name;
		this.store = new Store(store);
	}
	
	public FacetSort(String id, String store) {
		this(id, "", store);
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
	
	public RuleType getRuleType() {
		return ruleType;
	}

	public void setRuleType(RuleType ruleType) {
		this.ruleType = ruleType;
	}

	public List<FacetGroup> getFacetGroups() {
		return facetGroups;
	}

	public void setFacetGroups(List<FacetGroup> facetGroups) {
		this.facetGroups = facetGroups;
	}

	public String getStoreName(){
		if(getStore() != null)
			getStore().getStoreName();
		return "";
	}
	
	public String getFormattedCreatedDate() {
		return DateAndTimeUtils.formatDateTimeUsingConfig(getStoreName(), getCreatedDate());
	}
	
	public String getFormattedLastModifiedDate() {
		return DateAndTimeUtils.formatDateTimeUsingConfig(getStoreName(), getLastModifiedDate());
	}
	
	public String getReadableString(){
		return "";
	}
}