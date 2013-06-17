package com.search.manager.model;

import java.util.List;

public class SolrAttribute {
	
	private String id;
	private String name;
	private String displayName;
	private String searchable;
	private String facet;
	private String status;
	private String isRange;
	private List<SolrAttributeRange> list;
	
	public SolrAttribute(){}
	
	public SolrAttribute(String id, String name, String displayName, String searchable, String facet, String status, String isRange, List<SolrAttributeRange> list){
		this.id=id;
		this.name=name;
		this.displayName=displayName;
		this.searchable=searchable;
		this.facet=facet;
		this.status=status;
		this.isRange=isRange;
		this.list=list;
	}
	
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
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getSearchable() {
		return searchable;
	}
	public void setSearchable(String searchable) {
		this.searchable = searchable;
	}
	public String getFacet() {
		return facet;
	}
	public void setFacet(String facet) {
		this.facet = facet;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getIsRange() {
		return isRange;
	}
	public void setIsRange(String isRange) {
		this.isRange = isRange;
	}
	public List<SolrAttributeRange> getList() {
		return list;
	}
	public void setList(List<SolrAttributeRange> list) {
		this.list = list;
	}
}
