package com.search.manager.model;

import java.util.Date;

import com.search.manager.enums.MemberTypeEntity;
import com.search.manager.report.model.xml.ExcludeItemXml;

public class ExcludeResult extends SearchResult {

	private static final long serialVersionUID = 6843636825614943522L;
	
	public ExcludeResult() {}
	
	public ExcludeResult(StoreKeyword storeKeyword) {
		this.storeKeyword = storeKeyword;
	}
	
	public ExcludeResult(StoreKeyword storeKeyword, String edp, String comment, String createdBy, String lastModifiedBy, 
			Date expiryDate, Date createdDate, Date lastModifiedDate, String memberTypeId, String memberId) {
		super(storeKeyword, edp, comment, createdBy, lastModifiedBy, expiryDate, createdDate, lastModifiedDate, memberTypeId, memberId);
	}

	public ExcludeResult(StoreKeyword storeKeyword, ExcludeItemXml xml) {
		this.memberId = xml.getMemberId();
		this.storeKeyword = storeKeyword;
		this.edp = xml.getEdp();
		this.condition = xml.getRuleCondition();
		this.expiryDate = xml.getExpiryDate();
		this.entity = xml.getMemberType();
	}
	
	public MemberTypeEntity getExcludeEntity() {
		return getEntity();
	}

	public void setExcludeEntity(MemberTypeEntity excludeEntity) {
		setEntity(excludeEntity);
	}
}