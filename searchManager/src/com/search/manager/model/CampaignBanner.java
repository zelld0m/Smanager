package com.search.manager.model;

import java.util.List;

import org.joda.time.DateTime;

public class CampaignBanner extends ModelBean {
	private static final long serialVersionUID = -4219527385069110630L;
	
	private Campaign campaign;
	private Banner banner;
	private List<Keyword> keywords;
	private DateTime startDateTime;
	private DateTime endDateTime;
	
	public CampaignBanner() {
	}
	
	public CampaignBanner(Campaign campaign, Banner banner, List<Keyword> keywords, DateTime startDateTime, DateTime endDateTime,
			String createdBy, String lastModifiedBy, DateTime createdDateTime, DateTime lastModifiedDateTime) {
		this.campaign = campaign;
		this.banner = banner;
		this.keywords = keywords;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		this.createdBy = createdBy;
		this.createdDate = createdDateTime;
		this.lastModifiedBy = lastModifiedBy;
		this.lastModifiedDate = lastModifiedDateTime;
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