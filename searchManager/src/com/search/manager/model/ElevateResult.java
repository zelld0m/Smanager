package com.search.manager.model;

import java.util.Date;

public class ElevateResult extends ModelBean {
	
	private static final long serialVersionUID = -5261739293146842510L;

	private StoreKeyword storeKeyword;
	private String edp;
	private Integer location;
	private Date expiryDate;
	
	public ElevateResult() {
	}
	
	public ElevateResult(StoreKeyword storeKeyword, String edp, Integer location, String comment, String createdBy, String lastModifiedBy, Date expiryDate, Date createdDate, Date lastModifiedDate) {
		this.storeKeyword= storeKeyword;
		this.edp = edp;
		this.location = location;
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
}