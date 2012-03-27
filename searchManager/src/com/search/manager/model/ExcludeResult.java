package com.search.manager.model;

import java.util.Date;

public class ExcludeResult extends ModelBean {

	private static final long serialVersionUID = 6843636825614943522L;
	
	private StoreKeyword storeKeyword;
	private String edp;
	private Date expiryDate;
	
	public ExcludeResult() {
	}
	
	public ExcludeResult(StoreKeyword storeKeyword, String edp, String comment, String createdBy, String lastModifiedBy, Date expiryDate, Date createdDate, Date lastModifiedDate) {
		this.storeKeyword= storeKeyword;
		this.edp = edp;
		this.comment = comment;
		this.createdBy = createdBy;
		this.lastModifiedBy = lastModifiedBy;
		this.expiryDate = expiryDate;
		this.createdDate = createdDate;
		this.lastModifiedDate = lastModifiedDate;
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
}
