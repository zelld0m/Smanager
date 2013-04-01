package com.search.manager.model;

import java.io.Serializable;

import org.joda.time.DateTime;

import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.jodatime.JodaPatternType;

public class ModelBean implements Serializable {
	
	private static final long serialVersionUID = 1L;

	protected String comment;
	protected String createdBy;
	protected String lastModifiedBy;
	protected DateTime createdDateTime;
	protected DateTime lastModifiedDateTime;
	
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
	
	public String getFormattedCreatedDateTime() {
		return JodaDateTimeUtil.formatFromStorePattern(getCreatedDateTime(), JodaPatternType.DATE_TIME);
	}
	
	public String getFormattedLastModifiedDateTime() {
		return JodaDateTimeUtil.formatFromStorePattern(getLastModifiedDateTime(), JodaPatternType.DATE_TIME);
	}
	
	public String getFormattedCreatedDate() {
		return JodaDateTimeUtil.formatFromStorePattern(getCreatedDateTime(), JodaPatternType.DATE);
	}
	
	public String getFormattedLastModifiedDate() {
		return JodaDateTimeUtil.formatFromStorePattern(getLastModifiedDateTime(), JodaPatternType.DATE);
	}
}