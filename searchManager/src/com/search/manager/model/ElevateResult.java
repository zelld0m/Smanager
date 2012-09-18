package com.search.manager.model;

import java.util.Date;

import com.search.manager.enums.MemberTypeEntity;

public class ElevateResult extends SearchResult {
	
	private static final long serialVersionUID = -5261739293146842510L;

	private Integer location;
	private Boolean forceAdd;
	
	public ElevateResult() {
		super();
	}
	
	
	public ElevateResult(StoreKeyword storeKeyword) {
		this.storeKeyword = storeKeyword;
	}
	
	public ElevateResult(StoreKeyword storeKeyword, String memberId) {
		this.storeKeyword = storeKeyword;
		this.memberId = memberId;
	}
	
	public ElevateResult(StoreKeyword storeKeyword, String edp, Integer location, String comment, String createdBy, String lastModifiedBy, 
			Date expiryDate, Date createdDate, Date lastModifiedDate, String memberTypeId, String memberId, boolean forceAdd) {
		super(storeKeyword, edp, comment, createdBy, lastModifiedBy, expiryDate, createdDate, lastModifiedDate, memberTypeId, memberId);
		this.location = location;
		this.forceAdd = forceAdd;
	}
	
	@Override
	public String toString() {
		return "(StoreKeyword: " + storeKeyword + "\tEDP: " + edp + "\tlocation: " + location + ")";
	}
	
	public Integer getLocation() {
		return location;
	}
	
	public void setLocation(Integer location) {
		this.location = location;
	}
	
	public MemberTypeEntity getElevateEntity() {
		return getEntity();
	}

	public void setElevateEntity(MemberTypeEntity elevateEntity) {
		setEntity(elevateEntity);
	}

	public Boolean getForceAdd() {
		return forceAdd;
	}
	
	public Boolean isForceAdd() {
		return forceAdd;
	}

	public void setForceAdd(Boolean forceAdd) {
		this.forceAdd = forceAdd;
	}
}