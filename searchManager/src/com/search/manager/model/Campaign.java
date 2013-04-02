package com.search.manager.model;

import java.util.List;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;
import org.joda.time.DateTime;

import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.jodatime.JodaPatternType;

@DataTransferObject(converter = BeanConverter.class)
public class Campaign extends ModelBean {
	
	private static final long serialVersionUID = 1L;
	private String ruleId;
	private String ruleName;
	private List<Banner> bannerList;
	
	private Store store;
	private DateTime startDateTime;
	private DateTime endDateTime;
	
	private String description;
	
	public Campaign() {
	}
	
	public Campaign(String ruleId, String ruleName, Store store) {
		this.ruleId = ruleId;
		this.ruleName = ruleName;
		this.store = store;
	}
	
	public Campaign(String ruleId, String ruleName, Store store, DateTime startDateTime, DateTime endDateTime) {
		this.ruleId = ruleId;
		this.ruleName = ruleName;
		this.store = store;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
	}

	public Campaign(String ruleId, String ruleName, Store store, DateTime startDateTime, DateTime endDateTime, String comment,
			String createdBy, String lastModifiedBy, DateTime createdDateTime, DateTime lastModifiedDateTime) {
		this.ruleId = ruleId;
		this.ruleName = ruleName;
		this.store = store;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		this.comment = comment;
		this.createdBy = createdBy;
		this.lastModifiedBy = lastModifiedBy;
		this.createdDateTime = createdDateTime;
		this.lastModifiedDateTime = lastModifiedDateTime;
	}
	
	public Campaign(String ruleId, String ruleName,
			List<Banner> bannerList, Store store,
			DateTime startDateTime, DateTime endDateTime) {
		super();
		this.ruleId = ruleId;
		this.ruleName = ruleName;
		this.bannerList = bannerList;
		this.store = store;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
	}

	public Campaign(String ruleId, Store store) {
		this.ruleId = ruleId;
		this.store = store;
	}
	
	public Campaign(Store store, String ruleName) {
		this.store = store;
		this.ruleName = ruleName;
	}

	public Campaign(Store store, String ruleName, DateTime startDateTime,DateTime endDateTime, String description) {
		this.store = store;
		this.ruleName = ruleName;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		this.description = description;
	}

	public Campaign(Store store, String ruleId, String ruleName, DateTime startDateTime,DateTime endDateTime, String description) {
		this.store = store;
		this.ruleId = ruleId;
		this.ruleName = ruleName;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		this.description = description;
	}

	public List<Banner> getBannerList() {
		return bannerList;
	}

	public void setBannerList(List<Banner> bannerList) {
		this.bannerList = bannerList;
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

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	
	public String getRuleName() {
		return ruleName;
	}
	
	public void setStore(Store store) {
		this.store = store;
	}

	public Store getStore() {
		return store;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getRuleId() {
		return ruleId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFormattedStartDateTime() {
		return JodaDateTimeUtil.formatFromStorePattern(getStartDateTime(), JodaPatternType.DATE);
	}

	public String getFormattedEndDateTime() {
		return JodaDateTimeUtil.formatFromStorePattern(getEndDateTime(), JodaPatternType.DATE);
	}
}
