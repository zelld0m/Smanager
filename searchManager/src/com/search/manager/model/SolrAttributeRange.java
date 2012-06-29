package com.search.manager.model;

import java.util.List;

public class SolrAttributeRange {
	
	private String id;
	private String storeId;
	private String rangeValue;
	private String rangeSequence;
	private List<String> rangeValues;
	
	public SolrAttributeRange(){}
	
	public SolrAttributeRange(String id, String storeId, String rangeValue, String rangeSequence, List<String> rangeValues){
		this.id=id;
		this.storeId=storeId;
		this.rangeValue=rangeValue;
		this.rangeSequence=rangeSequence;
		this.rangeValues=rangeValues;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getRangeValue() {
		return rangeValue;
	}

	public void setRangeValue(String rangeValue) {
		this.rangeValue = rangeValue;
	}

	public String getRangeSequence() {
		return rangeSequence;
	}

	public void setRangeSequence(String rangeSequence) {
		this.rangeSequence = rangeSequence;
	}

	public List<String> getRangevalues() {
		return rangeValues;
	}

	public void setRangevalues(List<String> rangeValues) {
		this.rangeValues = rangeValues;
	}
}
