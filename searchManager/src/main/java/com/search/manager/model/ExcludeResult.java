package com.search.manager.model;

import org.joda.time.DateTime;

import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.report.model.xml.ExcludeItemXml;

public class ExcludeResult extends SearchResult {

	private static final long serialVersionUID = 6843636825614943522L;
	
	public ExcludeResult() {}
	
	public ExcludeResult(StoreKeyword storeKeyword) {
		this.storeKeyword = storeKeyword;
	}
	
	public ExcludeResult(StoreKeyword storeKeyword, String edp, String comment, String createdBy, String lastModifiedBy, 
			DateTime expiryDateTime, DateTime createdDateTime, DateTime lastModifiedDateTime, String memberTypeId, String memberId) {
		super(storeKeyword, edp, comment, createdBy, lastModifiedBy, expiryDateTime, createdDateTime, lastModifiedDateTime, memberTypeId, memberId);
	}

	public ExcludeResult(StoreKeyword storeKeyword, ExcludeItemXml xml) {
		this.memberId = xml.getMemberId();
		this.storeKeyword = storeKeyword;
		this.edp = xml.getEdp();
		this.condition = xml.getRuleCondition();
		this.expiryDate = xml.getExpiryDate();
		this.entity = xml.getMemberType();
		this.createdBy = xml.getCreatedBy();
		this.createdDate = xml.getCreatedDate();
		this.lastModifiedBy = xml.getLastModifiedBy();
		this.lastModifiedDate = xml.getLastModifiedDate();
	}
	
	public MemberTypeEntity getExcludeEntity() {
		return getEntity();
	}

	public void setExcludeEntity(MemberTypeEntity excludeEntity) {
		setEntity(excludeEntity);
	}
}