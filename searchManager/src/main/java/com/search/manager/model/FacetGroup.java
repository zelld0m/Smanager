package com.search.manager.model;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.core.model.ModelBean;
import com.search.manager.enums.FacetGroupType;
import com.search.manager.enums.SortType;
import com.search.manager.report.model.xml.FacetSortGroupXml;

@DataTransferObject(converter = BeanConverter.class)
public class FacetGroup extends ModelBean {

	private static final long serialVersionUID = -5244368611365335448L;
	
	private String ruleId;
	private String id;
	private String name;
	private FacetGroupType facetGroupType;
	private SortType sortType;
	private Integer sequence;
	private String storeId;
	
	public FacetGroup() {}
	
	public FacetGroup(String ruleId, String id, String name,
			FacetGroupType facetGroupType, SortType sortType, Integer sequence) {
		super();
		this.ruleId = ruleId;
		this.id = id;
		this.name = name;
		this.facetGroupType = facetGroupType;
		this.sortType = sortType;
		this.sequence = sequence;
	}
	
	public FacetGroup(String ruleId, String name,
			String facetGroupType, String sortType, Integer sequence) {
		this(ruleId, "", name, FacetGroupType.get(facetGroupType), SortType.get(sortType), sequence);
	}
	
	public FacetGroup(String ruleId, String id, String name, Integer sequence) {
		this(ruleId, id, name, null, null, sequence);
	}
	
	public FacetGroup(String ruleId, String id) {
		this(ruleId, id, null, null, null, null);
	}
	
	public FacetGroup(String ruleId, String id, String name,
			FacetGroupType facetGroupType, SortType sortType, Integer sequence,
			String storeId) {
		super();
		this.ruleId = ruleId;
		this.id = id;
		this.name = name;
		this.facetGroupType = facetGroupType;
		this.sortType = sortType;
		this.sequence = sequence;
		this.storeId = storeId;
	}
	
	public FacetGroup(FacetSortGroupXml xml, String ruleId, Integer sequence, String storeId){
		super();
		this.ruleId = ruleId;
		this.name = xml.getGroupName();
		this.facetGroupType = xml.getGroupType();
		this.sortType = xml.getSortType();
		this.sequence = sequence;
		this.storeId = storeId;
		setCreatedBy(xml.getCreatedBy());
		setCreatedDate(xml.getCreatedDate());
		//TODO add groupId
	}

	public FacetGroup(String id) {
		this("", id);
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
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

	public FacetGroupType getFacetGroupType() {
		return facetGroupType;
	}
	
	public void setFacetGroupType(FacetGroupType facetGroupType) {
		this.facetGroupType = facetGroupType;
	}
	
	public SortType getSortType() {
		return sortType;
	}
	
	public void setSortType(SortType sortType) {
		this.sortType = sortType;
	}
	
	public String getSortTypeLabel(){
		if(sortType != null){
			return sortType.getDisplayText();
		}
		return "";
	}
	
	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
}