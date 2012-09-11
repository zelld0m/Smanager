package com.search.manager.model;

import java.util.Date;

import com.search.manager.enums.MemberTypeEntity;

public class DemoteResult extends ModelBean {
	
	private static final long serialVersionUID = -5261739293146842510L;

	private StoreKeyword storeKeyword;
	private String edp;
	private Integer location;
	private Date expiryDate;
	private MemberTypeEntity demoteEntity;
	private RedirectRuleCondition condition;
	private String memberId;
	private Boolean forceAdd;
	
	public DemoteResult() {
	}
	
	public DemoteResult(StoreKeyword storeKeyword, String edp, Integer location, String comment, String createdBy, String lastModifiedBy, Date expiryDate, Date createdDate, Date lastModifiedDate, String memberTypeId, String memberId, boolean forceAdd) {
		if (memberTypeId.equals(MemberTypeEntity.FACET.toString())) {
			demoteEntity = MemberTypeEntity.FACET;
			condition = new RedirectRuleCondition(edp);
			if (storeKeyword != null) {
				condition.setStoreId(storeKeyword.getStoreId());
			}
			this.edp = "";
		} else {
			demoteEntity = MemberTypeEntity.PART_NUMBER;
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

	public MemberTypeEntity getDemoteEntity() {
		return demoteEntity;
	}

	public void setDemoteEntity(MemberTypeEntity demoteEntity) {
		this.demoteEntity = demoteEntity;
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

	public Boolean isForceAdd() {
		return forceAdd;
	}

	public void setForceAdd(Boolean forceAdd) {
		this.forceAdd = forceAdd;
	}
}