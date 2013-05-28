package com.search.manager.model;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;
import org.joda.time.DateTime;

import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.jodatime.JodaPatternType;

@DataTransferObject(converter = BeanConverter.class)
public class BannerRuleItem extends ModelBean{
	private static final long serialVersionUID = -2552014783905379956L;

	private BannerRule rule;
	private String memberId;
	private int priority;
	private DateTime startDate;
	private DateTime endDate;
	private String imageAlt;
	private String linkPath;
	private String openNewWindow;
	private String description;
	private Boolean disabled;
	private ImagePath imagePath;
	
	public BannerRuleItem() {
		// TODO Auto-generated constructor stub
	}
	
	public BannerRuleItem(BannerRule rule, String memberId,
			int priority, DateTime startDate, DateTime endDate,
			String imageAlt, String linkPath, String description,
			ImagePath imagePath, Boolean disabled) {
		super();
		this.rule = rule;
		this.memberId = memberId;
		this.priority = priority;
		this.startDate = startDate;
		this.endDate = endDate;
		this.imageAlt = imageAlt;
		this.linkPath = linkPath;
		this.description = description;
		this.imagePath = imagePath;
		this.disabled = disabled;
	}
	
	public BannerRuleItem(String ruleId, String storeId, String ruleName, String memberId,
			int priority, DateTime startDate, DateTime endDate,
			String imageAlt, String linkPath, String description,
			ImagePath imagePath, Boolean disabled) {
		this(new BannerRule(storeId, ruleId, ruleName, null), memberId, priority, startDate, endDate, imageAlt, linkPath, description, imagePath, disabled);
	}
	
	public BannerRuleItem(String ruleId, String storeId,  DateTime startDate, DateTime endDate){
		this(ruleId, storeId, null, null, 0, startDate, endDate, null, null, null, null, null);
	}
	
	public BannerRuleItem(String ruleId, String storeId, String memberId){
		this(ruleId, storeId, null, memberId, 0, null, null, null, null, null, null, null);
	}
	
	public BannerRule getRule() {
		return rule;
	}

	public void setRule(BannerRule rule) {
		this.rule = rule;
	}

	public String getMemberId() {
		return memberId;
	}
	
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public DateTime getStartDate() {
		return startDate;
	}
	
	public void setStartDate(DateTime startDate) {
		this.startDate = startDate;
	}
	
	public DateTime getEndDate() {
		return endDate;
	}
	
	public void setEndDate(DateTime endDate) {
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
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public ImagePath getImagePath() {
		return imagePath;
	}
	
	public void setImagePath(ImagePath imagePath) {
		this.imagePath = imagePath;
	}

	public String getOpenNewWindow() {
		return openNewWindow;
	}

	public void setOpenNewWindow(String openNewWindow) {
		this.openNewWindow = openNewWindow;
	}

	public Boolean getDisabled() {
		return disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}
	
	public String getFormattedStartDate(){
		return JodaDateTimeUtil.formatFromStorePattern(getStartDate(), JodaPatternType.DATE);
	}
	
	public String getFormattedStartDateTime(){
		return JodaDateTimeUtil.formatFromStorePattern(getStartDate(), JodaPatternType.DATE_TIME);
	}
	
	public String getFormattedEndDate(){
		return JodaDateTimeUtil.formatFromStorePattern(getStartDate(), JodaPatternType.DATE);
	}
	
	public String getFormattedEndDateTime(){
		return JodaDateTimeUtil.formatFromStorePattern(getStartDate(), JodaPatternType.DATE_TIME);
	}
}