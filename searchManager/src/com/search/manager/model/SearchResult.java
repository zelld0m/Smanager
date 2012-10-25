package com.search.manager.model;

import java.util.Date;

import com.search.manager.enums.MemberTypeEntity;

public class SearchResult extends ModelBean {

	private static final long serialVersionUID = 6843636825614943522L;

	protected StoreKeyword storeKeyword;
	protected String edp;
	protected Date expiryDate;
	protected MemberTypeEntity entity;
	protected RedirectRuleCondition condition;
	protected String memberId;
	
	public SearchResult() {}
	
	public SearchResult(StoreKeyword storeKeyword, String edp, String comment, String createdBy, String lastModifiedBy, Date expiryDate, Date createdDate, Date lastModifiedDate, String memberTypeId, String memberId) {
		if (memberTypeId.equals(MemberTypeEntity.FACET.toString())) {
			entity = MemberTypeEntity.FACET;
			condition = new RedirectRuleCondition(edp);
			if (storeKeyword != null) {
				condition.setStoreId(storeKeyword.getStoreId());
			}
			this.edp = "";
		} else {
			entity = MemberTypeEntity.PART_NUMBER;
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

	public MemberTypeEntity getEntity() {
		return entity;
	}

	public void setEntity(MemberTypeEntity entity) {
		this.entity = entity;
	}

	public RedirectRuleCondition getCondition() {
		return condition;
	}

	public void setCondition(RedirectRuleCondition condition) {
		this.condition = condition;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	
	public MemberTypeEntity getMemberType() {
		return this.entity;
	}

	public void setMemberType(MemberTypeEntity memberType) {
		this.entity = memberType;
	}
}