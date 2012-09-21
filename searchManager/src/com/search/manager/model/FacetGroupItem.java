package com.search.manager.model;

import java.io.Serializable;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter = BeanConverter.class)
public class FacetGroupItem implements Serializable{

	private static final long serialVersionUID = -5244368611365335448L;

	private FacetSort facetSort;
	private FacetGroup facetGroup;
	private String name;
	private Integer sequence;
	
	public FacetGroupItem() {}
	
	public FacetGroupItem(FacetSort facetSort, FacetGroup facetGroup,
			String name, Integer sequence) {
		super();
		this.facetSort = facetSort;
		this.facetGroup = facetGroup;
		this.name = name;
		this.sequence = sequence;
	}

	public FacetSort getFacetSort() {
		return facetSort;
	}


	public void setFacetSort(FacetSort facetSort) {
		this.facetSort = facetSort;
	}


	public FacetGroup getFacetGroup() {
		return facetGroup;
	}

	public void setFacetGroup(FacetGroup facetGroup) {
		this.facetGroup = facetGroup;
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
}