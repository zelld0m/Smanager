package com.search.manager.model;

import org.joda.time.DateTime;

public class Campaign extends ModelBean {
	
	private static final long serialVersionUID = 1L;
	private String campaignId;
	private String campaignName;
	private Store store;
	private DateTime startDateTime;
	private DateTime endDateTime;
	
	public Campaign() {
	}
	
	public Campaign(String campaignId, String campaignName, Store store) {
		this.campaignId = campaignId;
		this.campaignName = campaignName;
		this.store = store;
	}
	
	public Campaign(String campaignId, String campaignName, Store store, DateTime startDateTime, DateTime endDateTime) {
		this.campaignId = campaignId;
		this.campaignName = campaignName;
		this.store = store;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
	}

	public Campaign(String campaignId, String campaignName, Store store, DateTime startDateTime, DateTime endDateTime, String comment,
			String createdBy, String lastModifiedBy, DateTime createdDateTime, DateTime lastModifiedDateTime) {
		this.campaignId = campaignId;
		this.campaignName = campaignName;
		this.store = store;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		this.comment = comment;
		this.createdBy = createdBy;
		this.lastModifiedBy = lastModifiedBy;
		this.createdDateTime = createdDateTime;
		this.lastModifiedDateTime = lastModifiedDateTime;
	}
	
	public DateTime getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(DateTime startDateTime) {
		this.startDateTime = startDateTime;
	}

	public DateTime getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(DateTime endDateTime) {
		this.endDateTime = endDateTime;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}
	
	public String getCampaignName() {
		return campaignName;
	}
	
	public void setStore(Store store) {
		this.store = store;
	}

	public Store getStore() {
		return store;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	public String getCampaignId() {
		return campaignId;
	}
}