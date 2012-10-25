package com.search.manager.model;

import java.util.Date;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.report.model.xml.RuleVersionXml;

@DataTransferObject(converter = BeanConverter.class)
public class RuleVersionInfo {
	
	private String ruleId;
	private String ruleName;
	private String createdBy;
	private Date createdDate;
	private long version;
	private String notes;
	private String name;
	
	public RuleVersionInfo(RuleVersionXml xml){
		super();
		this.setRuleId(xml.getRuleId());
		this.setRuleName(xml.getRuleName());
		this.setName(xml.getName());
		this.setNotes(xml.getNotes());
		this.setVersion(xml.getVersion());
		this.setCreatedBy(xml.getCreatedBy());
		this.setCreatedDate(xml.getCreatedDate());
	}
	
	public String getRuleId() {
		return ruleId;
	}
	
	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}
	
	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public long getVersion() {
		return version;
	}
	
	public void setVersion(long version) {
		this.version = version;
	}
	
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
}