package com.search.manager.model;

import org.joda.time.DateTime;


public class Banner extends ModelBean {
	
	private static final long serialVersionUID = 1L;
	
	private Store store;
	private String bannerId;
	private String bannerName;
	private String linkPath;
	private String imagePath;
	
	public Banner() {
	}
	
	public Banner(String bannerId, String bannerName, Store store, String imagePath, String linkPath) {
		this.bannerId = bannerId;
		this.bannerName = bannerName;
		this.store = store;
		this.imagePath = imagePath;
		this.linkPath = linkPath;
	}
	
	public Banner(String bannerId, String bannerName, Store store, String imagePath, String linkPath, String comment,
			String createdBy, String lastModifiedBy, DateTime createdDateTime, DateTime lastModifiedDateTime) {
		this.bannerId = bannerId;
		this.bannerName = bannerName;
		this.store = store;
		this.imagePath = imagePath;
		this.linkPath = linkPath;
		this.comment = comment;
		this.createdBy = createdBy;
		this.lastModifiedBy = lastModifiedBy;
		this.createdDate = createdDateTime;
		this.lastModifiedDate = lastModifiedDateTime;
	}
	
	public String getBannerId() {
		return bannerId;
	}
	
	public void setBannerId(String bannerId) {
		this.bannerId = bannerId;
	}
	
	public String getBannerName() {
		return bannerName;
	}

	public void setBannerName(String bannerName) {
		this.bannerName = bannerName;
	}
	
	public String getLinkPath() {
		return linkPath;
	}
	
	public void setLinkPath(String linkPath) {
		this.linkPath = linkPath;
	}
	
	public String getImagePath() {
		return imagePath;
	}
	
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public Store getStore() {
		return store;
	}
	
}