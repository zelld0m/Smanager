package com.search.manager.core.model;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.solr.client.solrj.beans.Field;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.search.manager.core.SolrCore;
import com.search.manager.jodatime.JodaDateTimeUtil;
import com.search.manager.jodatime.JodaPatternType;
import com.search.manager.jodatime.jaxbadapter.DateTimeAdapter;

@SolrCore(name = "bannerrule")
@DataTransferObject(converter = BeanConverter.class)
public class BannerRuleItem extends ModelBean {

	private static final long serialVersionUID = -2552014783905379956L;

	private BannerRule rule;
	private String memberId;
	private Integer priority;
	private DateTime startDate;
	private DateTime endDate;
	private String imageAlt;
	private String linkPath;
	private Boolean openNewWindow;
	private String description;
	private Boolean disabled;
	private ImagePath imagePath;

	public BannerRuleItem() {
		// TODO Auto-generated constructor stub
	}

	public BannerRuleItem(BannerRule rule, String memberId, Integer priority,
			DateTime startDate, DateTime endDate, String imageAlt,
			String linkPath, String description, ImagePath imagePath,
			Boolean disabled, Boolean openNewWindow) {
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
		this.openNewWindow = openNewWindow;
	}

	public BannerRuleItem(String ruleId, String storeId, String ruleName,
			String memberId, int priority, DateTime startDate,
			DateTime endDate, String imageAlt, String linkPath,
			String description, ImagePath imagePath, Boolean disabled,
			Boolean openNewWindow) {
		this(new BannerRule(storeId, ruleId, ruleName, null), memberId,
				priority, startDate, endDate, imageAlt, linkPath, description,
				imagePath, disabled, openNewWindow);
	}

	public BannerRuleItem(String ruleId, String storeId, DateTime startDate,
			DateTime endDate) {
		this(ruleId, storeId, null, null, 0, startDate, endDate, null, null,
				null, null, null, null);
	}

	public BannerRuleItem(String ruleId, String storeId, String memberId) {
		this(ruleId, storeId, null, memberId, 0, null, null, null, null, null,
				null, null, null);
	}

	public BannerRuleItem(String ruleId, String storeId) {
		this(ruleId, storeId, null);
	}

	public BannerRule getRule() {
		return rule;
	}

	// @Field
	public void setRule(BannerRule rule) {
		this.rule = rule;
	}

	public String getMemberId() {
		return memberId;
	}

	public String getRuleName() {
		return this.rule.getRuleName();
	}

	@Field("ruleName")
	public void setRuleName(String ruleName) {
		if (this.rule != null) {
			this.rule.setRuleName(ruleName);
		}
	}

	@Field
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public Integer getPriority() {
		return priority;
	}

	@Field
	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public DateTime getStartDate() {
		return startDate;
	}

	// @Field
	public void setStartDate(DateTime startDate) {
		this.startDate = startDate;
	}

	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public DateTime getEndDate() {
		return endDate;
	}

	// @Field
	public void setEndDate(DateTime endDate) {
		this.endDate = endDate;
	}

	public String getImageAlt() {
		return imageAlt;
	}

	@Field
	public void setImageAlt(String imageAlt) {
		this.imageAlt = imageAlt;
	}

	public String getLinkPath() {
		return linkPath;
	}

	@Field
	public void setLinkPath(String linkPath) {
		this.linkPath = linkPath;
	}

	public String getDescription() {
		return description;
	}

	@Field
	public void setDescription(String description) {
		this.description = description;
	}

	public ImagePath getImagePath() {
		return imagePath;
	}

	// @Field
	public void setImagePath(ImagePath imagePath) {
		this.imagePath = imagePath;
	}

	public Boolean getOpenNewWindow() {
		return openNewWindow;
	}

	@Field
	public void setOpenNewWindow(Boolean openNewWindow) {
		this.openNewWindow = openNewWindow;
	}

	public Boolean getDisabled() {
		return disabled;
	}

	@Field
	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	public String getFormattedStartDate() {
		return JodaDateTimeUtil.formatFromStorePattern(getStartDate(),
				JodaPatternType.DATE);
	}

	public String getFormattedStartDateTime() {
		return JodaDateTimeUtil.formatFromStorePattern(getStartDate(),
				JodaPatternType.DATE_TIME);
	}

	public String getFormattedEndDate() {
		return JodaDateTimeUtil.formatFromStorePattern(getEndDate(),
				JodaPatternType.DATE);
	}

	public String getFormattedEndDateTime() {
		return JodaDateTimeUtil.formatFromStorePattern(getEndDate(),
				JodaPatternType.DATE_TIME);
	}

	public String getDaysLeft() {
		return JodaDateTimeUtil.getRemainingDays(DateTime.now(), getEndDate());
	}

	public boolean isExpired() {
		return DateTime.now().toDateMidnight()
				.isAfter(getEndDate().toDateMidnight());
	}

	public boolean isStarted() {
		return DateTime.now().toDateMidnight()
				.isAfter(getStartDate().toDateMidnight())
				|| DateTime.now().toDateMidnight()
						.isEqual(getStartDate().toDateMidnight());
	}

	// Solr Fields

	public DateTime getSolrStartDate() {
		return startDate.withZone(DateTimeZone.UTC);
	}

	@Field("startDate")
	public void setSolrStartDate(Date startDate) {
		this.startDate = JodaDateTimeUtil.toDateTime(startDate,
				DateTimeZone.UTC);
	}

	public DateTime getSolrEndDate() {
		return endDate.withZone(DateTimeZone.UTC);
	}

	@Field("endDate")
	public void setSolrEndDate(Date endDate) {
		this.endDate = JodaDateTimeUtil.toDateTime(endDate, DateTimeZone.UTC);
	}

	public String getBannerStoreId() {
		if (this.rule != null) {
			return this.rule.getStoreId();
		}
		return null;
	}

	@Field("store")
	public void setBannerStoreId(String bannerStoreId) {
		if (this.rule == null) {
			this.rule = new BannerRule();
		}
		this.rule.setStoreId(bannerStoreId);
	}
	
	public String getBannerRuleId() {
		if (this.rule != null) {
			return this.rule.getRuleId();
		}
		return null;
	}

	@Field("ruleId")
	public void setBannerRuleId(String bannerRuleId) {
		if (this.rule == null) {
			this.rule = new BannerRule();
		}
		this.rule.setRuleId(bannerRuleId);
	}

	public String getBannerRuleName() {
		if (this.rule != null) {
			return this.rule.getRuleName();
		}
		return null;
	}

	@Field("ruleName")
	public void setBannerRuleName(String bannerRuleName) {
		if (this.rule == null) {
			this.rule = new BannerRule();
		}
		this.rule.setRuleName(bannerRuleName);
	}

	public String getImagePathId() {
		if (this.imagePath != null) {
			return this.imagePath.getId();
		}
		return null;
	}

	@Field("imagePathId")
	public void setImagePathId(String imagePathId) {
		if (this.imagePath == null) {
			this.imagePath = new ImagePath();
		}
		this.imagePath.setId(imagePathId);
	}

	public String getImagePathPath() {
		if (this.imagePath != null) {
			return this.imagePath.getPath();
		}
		return null;
	}

	@Field("path")
	public void setImagePathPath(String imagePathPath) {
		if (this.imagePath == null) {
			this.imagePath = new ImagePath();
		}
		this.imagePath.setPath(imagePathPath);
	}

	public String getImagePathSize() {
		if (this.imagePath != null) {
			return this.imagePath.getSize();
		}
		return null;
	}

	@Field("size")
	public void setImagePathSize(String imagePathSize) {
		if (this.imagePath == null) {
			this.imagePath = new ImagePath();
		}
		this.imagePath.setSize(imagePathSize);
	}

	public ImagePathType getImagePathPathType() {
		if (this.imagePath != null) {
			return this.imagePath.getPathType();
		}
		return null;
	}

	@Field("pathType")
	public void setImagePathPathType(String imagePathType) {
		if (this.imagePath == null) {
			this.imagePath = new ImagePath();
		}

		this.imagePath.setPathType(ImagePathType.get(imagePathType));
	}

	public String getImagePathAlias() {
		if (this.imagePath != null) {
			return this.imagePath.getAlias();
		}
		return null;
	}

	@Field("alias")
	public void setImagePathAlias(String ImagePathAlias) {
		if (this.imagePath == null) {
			this.imagePath = new ImagePath();
		}
		this.imagePath.setAlias(ImagePathAlias);
	}

	public String getSolrId() {
		return this.rule.getStoreId() + "_" + this.rule.getRuleId() + "_"
				+ this.getMemberId();
	}

	@Field("id")
	public void setSolrId(String solrId) {
		// solr input document id
	}

}