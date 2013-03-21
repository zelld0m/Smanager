package com.search.manager.model;

import java.io.Serializable;

import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

public class ModelBean implements Serializable {
	
	private static final long serialVersionUID = 1L;

	protected String comment;
	protected String createdBy;
	protected String lastModifiedBy;
	@DateTimeFormat protected DateTime createdDateTime;
	@DateTimeFormat protected DateTime lastModifiedDateTime;
	
	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
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
	
	public DateTime getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(DateTime createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public DateTime getLastModifiedDateTime() {
		return lastModifiedDateTime;
	}

	public void setLastModifiedDateTime(DateTime lastModifiedDateTime) {
		this.lastModifiedDateTime = lastModifiedDateTime;
	}
}