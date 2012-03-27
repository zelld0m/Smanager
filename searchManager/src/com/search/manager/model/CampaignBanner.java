package com.search.manager.model;

import java.util.Date;
import java.util.List;

public class CampaignBanner extends ModelBean {
	
	public CampaignBanner() {
	}
	
	public CampaignBanner(Campaign campaign, Banner banner, List<Keyword> keywords, Date startDate, Date endDate,
			String createdBy, String lastModifiedBy, Date createdDate, Date lastModifiedDate) {
		this.campaign = campaign;
		this.banner = banner;
		this.keywords = keywords;
		this.startDate = startDate;
		this.endDate = endDate;
		this.createdBy = createdBy;
		this.createdDate = createdDate;
		this.lastModifiedBy = lastModifiedBy;
		this.lastModifiedDate = lastModifiedDate;
	}

	private static final long serialVersionUID = -4219527385069110630L;
	
	private Campaign campaign;
	private Banner banner;
	private List<Keyword> keywords;
	private Date startDate;
	private Date endDate;
	
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

}
