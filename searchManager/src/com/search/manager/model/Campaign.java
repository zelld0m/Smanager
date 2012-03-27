package com.search.manager.model;

import java.util.Date;

public class Campaign extends ModelBean {
	
	private static final long serialVersionUID = 1L;
	private String campaignId;
	private String campaignName;
	private Store store;
	private Date startDate;
	private Date endDate;
	
	public Campaign() {
	}
	
	public Campaign(String campaignId, String campaignName, Store store) {
		this.campaignId = campaignId;
		this.campaignName = campaignName;
		this.store = store;
	}
	
	public Campaign(String campaignId, String campaignName, Store store, Date startDate, Date endDate) {
		this.campaignId = campaignId;
		this.campaignName = campaignName;
		this.store = store;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public Campaign(String campaignId, String campaignName, Store store, Date startDate, Date endDate, String comment,
			String createdBy, String lastModifiedBy, Date createdDate, Date lastModifiedDate) {
		this.campaignId = campaignId;
		this.campaignName = campaignName;
		this.store = store;
		this.startDate = startDate;
		this.endDate = endDate;
		this.comment = comment;
		this.createdBy = createdBy;
		this.lastModifiedBy = lastModifiedBy;
		this.createdDate = createdDate;
		this.lastModifiedDate = lastModifiedDate;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
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
