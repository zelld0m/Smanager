package com.search.reports.manager.model;

import org.joda.time.DateTime;

public class LogProcess {
	String createdBy;
	DateTime createdStamp;
	DateTime createdTxStamp;
	String lastUpdatedBy;
	DateTime lastUpdatedStamp;
	DateTime lastUpdatedTxStamp;

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public DateTime getCreatedStamp() {
		return createdStamp;
	}

	public void setCreatedStamp(DateTime createdStamp) {
		this.createdStamp = createdStamp;
	}

	public DateTime getCreatedTxStamp() {
		return createdTxStamp;
	}

	public void setCreatedTxStamp(DateTime createdTxStamp) {
		this.createdTxStamp = createdTxStamp;
	}

	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public DateTime getLastUpdatedStamp() {
		return lastUpdatedStamp;
	}

	public void setLastUpdatedStamp(DateTime lastUpdatedStamp) {
		this.lastUpdatedStamp = lastUpdatedStamp;
	}

	public DateTime getLastUpdatedTxStamp() {
		return lastUpdatedTxStamp;
	}

	public void setLastUpdatedTxStamp(DateTime lastUpdatedTxStamp) {
		this.lastUpdatedTxStamp = lastUpdatedTxStamp;
	}

}
