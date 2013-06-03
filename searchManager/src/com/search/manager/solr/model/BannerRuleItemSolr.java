package com.search.manager.solr.model;

import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

public class BannerRuleItemSolr {

	@Field
	private String store;
	@Field
	private String ruleId;
	@Field
	private String ruleName;
	@Field
	private String memberId;
	@Field
	private Integer priority;
	@Field
	private Date startDate;
	@Field
	private Date endDate;
	@Field
	private String imageAlt;
	@Field
	private String linkPath;
	@Field
	private boolean openNewWindow;
	@Field
	private String description;
	@Field
	private boolean disabled;
	@Field
	private String imagePathId;
	@Field
	private String path;
	@Field
	private String pathType;
	@Field
	private String alias;

	public String getStore() {
		return store;
	}

	public void setStore(String store) {
		this.store = store;
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

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
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

	public String getImageAlt() {
		return imageAlt;
	}

	public void setImageAlt(String imageAlt) {
		this.imageAlt = imageAlt;
	}

	public String getLinkPath() {
		return linkPath;
	}

	public void setLinkPath(String linkPath) {
		this.linkPath = linkPath;
	}

	public boolean isOpenNewWindow() {
		return openNewWindow;
	}

	public void setOpenNewWindow(boolean openNewWindow) {
		this.openNewWindow = openNewWindow;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public String getImagePathId() {
		return imagePathId;
	}

	public void setImagePathId(String imagePathId) {
		this.imagePathId = imagePathId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPathType() {
		return pathType;
	}

	public void setPathType(String pathType) {
		this.pathType = pathType;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

}
