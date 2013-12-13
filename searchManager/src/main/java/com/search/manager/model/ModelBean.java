package com.search.manager.model;

import java.io.Serializable;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.DateTime;

import com.search.manager.jodatime.jaxbadapter.DateTimeAdapter;

public class ModelBean implements Serializable {
	
	private static final long serialVersionUID = 1L;

	protected String comment;
	protected String createdBy;
	protected String lastModifiedBy;
	protected DateTime createdDate;
	protected DateTime lastModifiedDate;
	
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

	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public DateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(DateTime createdDate) {
		this.createdDate = createdDate;
	}

	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public DateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(DateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
}