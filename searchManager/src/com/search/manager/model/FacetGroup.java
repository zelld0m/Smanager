package com.search.manager.model;

import java.util.List;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.enums.FacetGroupType;
import com.search.manager.enums.SortType;

@DataTransferObject(converter = BeanConverter.class)
public class FacetGroup extends ModelBean {

	private static final long serialVersionUID = -5244368611365335448L;

	private FacetSort facetSort;
	private FacetGroupType facetGroupType;
	private SortType sortType;
	private List<FacetGroupItem> facetGroupItems;
	
	public FacetGroup() {}
	
	public FacetGroup(FacetSort facetSort, FacetGroupType facetGroupType,
			SortType sortType, List<FacetGroupItem> facetGroupItems) {
		super();
		this.facetSort = facetSort;
		this.facetGroupType = facetGroupType;
		this.sortType = sortType;
		this.facetGroupItems = facetGroupItems;
	}

	public List<FacetGroupItem> getFacetGroupItems() {
		return facetGroupItems;
	}

	public void setFacetGroupItems(List<FacetGroupItem> facetGroupItems) {
		this.facetGroupItems = facetGroupItems;
	}

	public FacetSort getFacetSort() {
		return facetSort;
	}

	public void setFacetSort(FacetSort facetSort) {
		this.facetSort = facetSort;
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
}