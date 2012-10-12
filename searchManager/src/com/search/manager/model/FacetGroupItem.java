package com.search.manager.model;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter = BeanConverter.class)
public class FacetGroupItem extends ModelBean{

	private static final long serialVersionUID = -5244368611365335448L;

	private String ruleId;
	private String facetGroupId;
	private String memberId;
	private String name;
	private Integer sequence;
	private String storeId;
	private FacetGroup facetGroup;
	
	public FacetGroupItem() {}

	public FacetGroupItem(String ruleId, String facetGroupId, String memberId, String name,
			Integer sequence) {
		super();
		this.ruleId = ruleId;
		this.facetGroupId = facetGroupId;
		this.memberId = memberId;
		this.name = name;
		this.sequence = sequence;
	}
	
	public FacetGroupItem(String facetGroupId, String memberId, String name,
			Integer sequence) {
		this("", facetGroupId, memberId, name, sequence);
	}
	
	public FacetGroupItem(String memberId, String name,	Integer sequence) {
		super();
		this.memberId = memberId;
		this.name = name;
		this.sequence = sequence;
	}
	
	public FacetGroupItem(String ruleId, String facetGroupId) {
		super();
		this.ruleId = ruleId;
		this.facetGroupId = facetGroupId;
	}
	
	public FacetGroupItem(String memberId) {
		super();
		this.memberId = memberId;
	}

	public String getId() {
		return memberId;
	}

	public void setId(String id) {
		this.memberId = id;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getFacetGroupId() {
		return facetGroupId;
	}

	public void setFacetGroupId(String facetGroupId) {
		this.facetGroupId = facetGroupId;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Integer getSequence() {
		return sequence;
	}
	
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public FacetGroup getFacetGroup() {
		return facetGroup;
	}

	public void setFacetGroup(FacetGroup facetGroup) {
		this.facetGroup = facetGroup;
	}
	
	public String getFacetGroupName(){
		if(getFacetGroup() != null)
			return getFacetGroup().getName();
		return null;
	}
	
	public String getFacetGroupSortOrder(){
		if(getFacetGroup() != null && getFacetGroup().getSortType() != null)
			return getFacetGroup().getSortType().getDisplayText();
		return null;
	}
	
	
}