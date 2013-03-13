package com.search.manager.model;

import org.joda.time.DateTime;

import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.report.model.xml.ElevateItemXml;

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
			DateTime expiryDateTime, DateTime createdDateTime, DateTime lastModifiedDateTime, String memberTypeId, String memberId, boolean forceAdd) {
		super(storeKeyword, edp, comment, createdBy, lastModifiedBy, expiryDateTime, createdDateTime, lastModifiedDateTime, memberTypeId, memberId);
		this.location = location;
		this.forceAdd = forceAdd;
	}
	
	public ElevateResult(StoreKeyword storeKeyword, ElevateItemXml xml) {
		this.memberId = xml.getMemberId();
		this.storeKeyword = storeKeyword;
		this.edp = xml.getEdp();
		this.condition = xml.getRuleCondition();
		this.expiryDateTime = xml.getExpiryDateTime();
		this.entity = xml.getMemberType();
		this.location = xml.getLocation();
		this.forceAdd = xml.isForceAdd();
		this.createdBy = xml.getCreatedBy();
		this.createdDateTime = xml.getCreatedDateTime();
		this.lastModifiedBy = xml.getLastModifiedBy();
		this.lastModifiedDateTime = xml.getLastModifiedDateTime();
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