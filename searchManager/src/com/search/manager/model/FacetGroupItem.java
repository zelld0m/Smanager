package com.search.manager.model;

import java.io.Serializable;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter = BeanConverter.class)
public class FacetGroupItem implements Serializable{

	private static final long serialVersionUID = -5244368611365335448L;

	private String memberId;
	private String facetGroupId;
	private String name;
	private Integer sequence;
	
	public FacetGroupItem() {}

	public FacetGroupItem(String memberId, String facetGroupId, String name,
			Integer sequence) {
		super();
		this.memberId = memberId;
		this.facetGroupId = facetGroupId;
		this.name = name;
		this.sequence = sequence;
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
}