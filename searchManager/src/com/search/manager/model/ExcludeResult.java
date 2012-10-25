package com.search.manager.model;

import java.util.Date;

import com.search.manager.enums.MemberTypeEntity;

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
	
	public MemberTypeEntity getExcludeEntity() {
		return getEntity();
	}

	public void setExcludeEntity(MemberTypeEntity excludeEntity) {
		setEntity(excludeEntity);
	}

}
