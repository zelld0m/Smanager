package com.search.manager.report.model.xml;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "rankingrule")
public class RankingRuleXml {
	
	public static final long SERIAL_VERSION_UID = 1L;

	private String ruleId;
	private String ruleName;
	private String description;
	private String createdBy;
	private String modifiedBy;
	private Date createdDate;
	private Date lastModifiedDate;
	private Date startDate;
	private Date endDate;
	private Map<String,String> relevancyFields;
	private List<RankingRuleKeywordXml> keywords;
	private long serVersion;
	private String reason;
	
	public RankingRuleXml() {
		this.serVersion = SERIAL_VERSION_UID;
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
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
	public Map<String, String> getRelevancyFields() {
		return relevancyFields;
	}
	public void setRelevancyFields(Map<String, String> relevancyFields) {
		this.relevancyFields = relevancyFields;
	}
	public List<RankingRuleKeywordXml> getKeywords() {
		return keywords;
	}
	public void setKeywords(List<RankingRuleKeywordXml> keywords) {
		this.keywords = keywords;
	}
	public long getVersion() {
		return serVersion;
	}
	public void setVersion(long version) {
		this.serVersion = version;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
}
