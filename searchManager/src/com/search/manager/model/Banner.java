package com.search.manager.model;

import java.util.Date;
import java.util.List;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter = BeanConverter.class)
public class Banner extends ModelBean {
	
	private static final long serialVersionUID = 1L;
	
	private Store store;
	private String bannerId;
	private String bannerName;
	private String linkPath;
	private String imagePath;
	private String thumbnailPath;
	private String imageAlt;
	private List<Keyword> keywordList;
	
	public Banner() {
	}
	
	public Banner(String bannerId, String bannerName, Store store) {
		this.bannerId = bannerId;
		this.bannerName = bannerName;
		this.store = store;
	}
	
	public Banner(String bannerId, String bannerName, Store store, String imagePath, String linkPath) {
		this.bannerId = bannerId;
		this.bannerName = bannerName;
		this.store = store;
		this.imagePath = imagePath;
		this.linkPath = linkPath;
	}
	
	public Banner(String bannerId, String bannerName, Store store, String imagePath, String linkPath, String comment,
			String createdBy, String lastModifiedBy, Date createdDate, Date lastModifiedDate) {
		this.bannerId = bannerId;
		this.bannerName = bannerName;
		this.store = store;
		this.imagePath = imagePath;
		this.linkPath = linkPath;
		this.comment = comment;
		this.createdBy = createdBy;
		this.lastModifiedBy = lastModifiedBy;
		this.createdDate = createdDate;
		this.lastModifiedDate = lastModifiedDate;
	}
	
	public Banner(Store store, String bannerId, String bannerName,
			String linkPath, String imagePath, String thumbnailPath,
			String imageAlt, List<Keyword> keywordList) {
		super();
		this.store = store;
		this.bannerId = bannerId;
		this.bannerName = bannerName;
		this.linkPath = linkPath;
		this.imagePath = imagePath;
		this.thumbnailPath = thumbnailPath;
		this.imageAlt = imageAlt;
		this.keywordList = keywordList;
	}

	public Banner(Store store, String bannerId, String bannerName,
			String linkPath, String imagePath, String imageAlt) {
		super();
		this.store = store;
		this.bannerId = bannerId;
		this.bannerName = bannerName;
		this.linkPath = linkPath;
		this.imagePath = imagePath;
		this.imageAlt = imageAlt;
	}
	
	public Banner(String storeId, String bannerName,
			String linkPath, String imagePath, String description, String createdBy) {
		super();
		this.store = new Store(storeId);
		this.bannerName = bannerName;
		this.linkPath = linkPath;
		this.imagePath = imagePath;
		this.comment = description;
		this.createdBy = createdBy;
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

	public String getImageAlt() {
		return imageAlt;
	}

	public void setImageAlt(String imageAlt) {
		this.imageAlt = imageAlt;
	}

	public String getThumbnailPath() {
		return thumbnailPath;
	}

	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}

	public List<Keyword> getKeywordList() {
		return keywordList;
	}

	public void setKeywordList(List<Keyword> keywordList) {
		this.keywordList = keywordList;
	}
}