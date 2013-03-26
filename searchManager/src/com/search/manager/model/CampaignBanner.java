package com.search.manager.model;

import java.util.List;

import org.joda.time.DateTime;

public class CampaignBanner extends ModelBean {
	private static final long serialVersionUID = -4219527385069110630L;
	
	private Campaign campaign;
	private Banner banner;
	private Store store;
	private List<Keyword> keywords;
	private DateTime startDateTime;
	private DateTime endDateTime;
	
	public CampaignBanner() {
	}
	
	public CampaignBanner(Campaign campaign, Banner banner, Store store) {
		super();
		this.campaign = campaign;
		this.banner = banner;
		this.store = store;
	}

	public CampaignBanner(Campaign campaign, Banner banner, Store store,
			List<Keyword> keywords, DateTime startDateTime, DateTime endDateTime) {
		super();
		this.campaign = campaign;
		this.banner = banner;
		this.store = store;
		this.keywords = keywords;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
	}

	public CampaignBanner(Campaign campaign, Banner banner, List<Keyword> keywords, DateTime startDateTime, DateTime endDateTime,
			String createdBy, String lastModifiedBy, DateTime createdDateTime, DateTime lastModifiedDateTime) {
		this.campaign = campaign;
		this.banner = banner;
		this.keywords = keywords;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		this.createdBy = createdBy;
		this.createdDateTime = createdDateTime;
		this.lastModifiedBy = lastModifiedBy;
		this.lastModifiedDateTime = lastModifiedDateTime;
	}
	
	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public Campaign getCampaign() {
		return campaign;
	}
	
	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}
	
	public Banner getBanner() {
		return banner;
	}
	
	public void setBanner(Banner banner) {
		this.banner = banner;
	}
	
	public List<Keyword> getKeywords() {
		return keywords;
	}
	
	public void setKeywords(List<Keyword> keywords) {
		this.keywords = keywords;
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
}