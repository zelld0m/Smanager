package com.search.manager.report.model.xml;

import java.io.Serializable;

import org.joda.time.DateTime;

public class BaseEntityXml implements Serializable{
	private static final long serialVersionUID = 8767443073742555984L;
	private String createdBy;
	private String lastModifiedBy;
	private DateTime lastModifiedDate;
	private DateTime createdDate;
	
	public String getCreatedBy() {
		return createdBy;
	}
	
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	public String getLastModifiedBy() {
		return lastModifiedBy;
	}
	
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public DateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(DateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public DateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(DateTime createdDate) {
		this.createdDate = createdDate;
	}
}