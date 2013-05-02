package com.search.manager.model;

import java.util.List;
import org.joda.time.DateTime;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter = BeanConverter.class)
public class Banner extends ModelBean {
	
	private static final long serialVersionUID = 1L;
	
	private Store store;
	private String ruleId;
	private String ruleName;
	private String description;
	private String linkPath;
	private String imagePath;
	private String thumbnailPath;
	private String imageAlt;
	private List<Keyword> keywordList;
	
	public Banner() {}
	
	public Banner(Store store, String ruleId, String ruleName, String description){
		this.store = store;
		this.ruleId = ruleId;
		this.ruleName = ruleName;
		this.description = description;
	}
	
	public Banner(String ruleId, String ruleName, Store store) {
		this.ruleId = ruleId;
		this.ruleName = ruleName;
		this.store = store;
	}
	
	public Banner(String ruleId, String ruleName, Store store, String imagePath, String linkPath) {
		this.ruleId = ruleId;
		this.ruleName = ruleName;
		this.store = store;
		this.imagePath = imagePath;
		this.linkPath = linkPath;
	}
	
	public Banner(String ruleId, String ruleName, Store store, String imagePath, String linkPath, String description,
			String createdBy, String lastModifiedBy, DateTime createdDateTime, DateTime lastModifiedDateTime) {
		this.ruleId = ruleId;
		this.ruleName = ruleName;
		this.store = store;
		this.imagePath = imagePath;
		this.linkPath = linkPath;
		this.description = description;
		this.createdBy = createdBy;
		this.lastModifiedBy = lastModifiedBy;
		this.createdDate = createdDateTime;
		this.lastModifiedDate = lastModifiedDateTime;
	}
	
	public Banner(Store store, String ruleId, String ruleName,
			String linkPath, String imagePath, String thumbnailPath,
			String imageAlt, List<Keyword> keywordList) {
		super();
		this.store = store;
		this.ruleId = ruleId;
		this.ruleName = ruleName;
		this.linkPath = linkPath;
		this.imagePath = imagePath;
		this.thumbnailPath = thumbnailPath;
		this.imageAlt = imageAlt;
		this.keywordList = keywordList;
	}

	public Banner(Store store, String ruleId, String ruleName,
			String linkPath, String imagePath, String imageAlt) {
		super();
		this.store = store;
		this.ruleId = ruleId;
		this.ruleName = ruleName;
		this.linkPath = linkPath;
		this.imagePath = imagePath;
		this.imageAlt = imageAlt;
	}
	
	public Banner(String storeId, String ruleName,
			String linkPath, String imagePath, String description, String createdBy) {
		super();
		this.store = new Store(storeId);
		this.ruleName = ruleName;
		this.linkPath = linkPath;
		this.imagePath = imagePath;
		this.description = description;
		this.createdBy = createdBy;
	}

	public Banner(String ruleId, Store store) {
		this.store = store;
		this.ruleId = ruleId;
	}

	public Banner(Store store, String ruleName) {
		this.store = store;
		this.ruleName = ruleName;
	}

	public String getRuleId() {
		return ruleId;
	}
	
	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}
	
	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}