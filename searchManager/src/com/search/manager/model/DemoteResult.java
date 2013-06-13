package com.search.manager.model;

import org.joda.time.DateTime;

import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.report.model.xml.DemoteItemXml;

public class DemoteResult extends SearchResult {
	
	private static final long serialVersionUID = -5261739293146842510L;

	private Integer location;
	
	public DemoteResult() { }
	
	public DemoteResult(StoreKeyword storeKeyword) {
		this.storeKeyword = storeKeyword;
	}
	
	public DemoteResult(String memberId){
		this.memberId = memberId;
	}
	
	public DemoteResult(StoreKeyword storeKeyword, String edp, Integer location, String comment, String createdBy, String lastModifiedBy, DateTime expiryDateTime, DateTime createdDateTime, DateTime lastModifiedDateTime, String memberTypeId, String memberId) {
		super(storeKeyword, edp, comment, createdBy, lastModifiedBy, expiryDateTime, createdDateTime, lastModifiedDateTime, memberTypeId, memberId);
		this.location = location;
	}
	
	public DemoteResult(StoreKeyword storeKeyword, DemoteItemXml xml) {
		this.memberId = xml.getMemberId();
		this.storeKeyword = storeKeyword;
		this.edp = xml.getEdp();
		this.condition = xml.getRuleCondition();
		this.expiryDateTime = xml.getExpiryDateTime();
		this.entity = xml.getMemberType();
		this.location = xml.getLocation();
		this.createdBy = xml.getCreatedBy();
		this.createdDate = xml.getCreatedDate();
		this.lastModifiedBy = xml.getLastModifiedBy();
		this.lastModifiedDate = xml.getLastModifiedDate();
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
	
	public MemberTypeEntity getDemoteEntity() {
		return getEntity();
	}

	public void setDemoteEntity(MemberTypeEntity demoteEntity) {
		setEntity(demoteEntity);
	}
}