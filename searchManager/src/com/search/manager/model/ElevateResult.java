package com.search.manager.model;

import java.util.Date;

import com.search.manager.enums.MemberTypeEntity;

public class ElevateResult extends ModelBean {
	
	private static final long serialVersionUID = -5261739293146842510L;

	private StoreKeyword storeKeyword;
	private String edp;
	private Integer location;
	private Date expiryDate;
	private MemberTypeEntity elevateEntity;
	private String condition;
	private String memberId;
	private Boolean forceAdd;
	
	public ElevateResult() {
	}
	
	public ElevateResult(StoreKeyword storeKeyword, String edp, Integer location, String comment, String createdBy, String lastModifiedBy, Date expiryDate, Date createdDate, Date lastModifiedDate, String memberTypeId, String memberId, boolean forceAdd) {
		if (memberTypeId.equals(MemberTypeEntity.FACET.toString())) {
			elevateEntity = MemberTypeEntity.FACET;
			this.condition = edp;
			this.edp = "";
		} else {
			elevateEntity = MemberTypeEntity.PART_NUMBER;
			this.edp = edp;
		}
		this.storeKeyword= storeKeyword;
		this.location = location;
		this.comment = comment;
		this.createdBy = createdBy;
		this.lastModifiedBy = lastModifiedBy;
		this.expiryDate = expiryDate;
		this.createdDate = createdDate;
		this.lastModifiedDate = lastModifiedDate;
		this.memberId = memberId;
		this.forceAdd = forceAdd;
	}
	
	public StoreKeyword getStoreKeyword() {
		return storeKeyword;
	}
	
	public void setStoreKeyword(StoreKeyword storeKeyword) {
		this.storeKeyword = storeKeyword;
	}
	
	public String getEdp() {
		return edp;
	}
	
	public void setEdp(String edp) {
		this.edp = edp;
	}
	
	public Integer getLocation() {
		return location;
	}
	
	public void setLocation(Integer location) {
		this.location = location;
	}
	
	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
	
	@Override
	public String toString() {
		return "(StoreKeyword: " + storeKeyword + "\tEDP: " + edp + "\tlocation: " + location + ")";
	}

	public MemberTypeEntity getElevateEntity() {
		return elevateEntity;
	}

	public void setElevateEntity(MemberTypeEntity elevateEntity) {
		this.elevateEntity = elevateEntity;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public Boolean isForceAdd() {
		return forceAdd;
	}

	public void setForceAdd(Boolean forceAdd) {
		this.forceAdd = forceAdd;
	}
}