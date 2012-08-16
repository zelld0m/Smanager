package com.search.manager.model;

import java.util.Date;

import com.search.manager.enums.MemberTypeEntity;

public class ExcludeResult extends ModelBean {

	private static final long serialVersionUID = 6843636825614943522L;
	
	private StoreKeyword storeKeyword;
	private String edp;
	private Date expiryDate;
	private MemberTypeEntity excludeEntity;
	private String condition;
	private String memberId;
	
	public ExcludeResult() {
	}
	
	public ExcludeResult(StoreKeyword storeKeyword, String edp, String comment, String createdBy, String lastModifiedBy, Date expiryDate, Date createdDate, Date lastModifiedDate, String memberTypeId, String memberId) {
		if (memberTypeId.equals(MemberTypeEntity.FACET.toString())) {
			excludeEntity = MemberTypeEntity.FACET;
			this.condition = edp;
			this.edp = "";
		} else {
			excludeEntity = MemberTypeEntity.PART_NUMBER;
			this.edp = edp;
		}
		this.storeKeyword= storeKeyword;
		this.comment = comment;
		this.createdBy = createdBy;
		this.lastModifiedBy = lastModifiedBy;
		this.expiryDate = expiryDate;
		this.createdDate = createdDate;
		this.lastModifiedDate = lastModifiedDate;
		this.memberId = memberId;
	}
	
	public StoreKeyword getStoreKeyword() {
		return storeKeyword;
	}
	
	public void setStoreKeyword(StoreKeyword storeKeyword) {
		this.storeKeyword = storeKeyword;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getEdp() {
		return edp;
	}
	
	public void setEdp(String edp) {
		this.edp = edp;
	}

	public MemberTypeEntity getExcludeEntity() {
		return excludeEntity;
	}

	public void setExcludeEntity(MemberTypeEntity excludeEntity) {
		this.excludeEntity = excludeEntity;
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
}
