package com.search.manager.model;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.report.model.xml.RuleXml;

@DataTransferObject(converter = BeanConverter.class)
public class RuleVersionInfo extends ModelBean{

	private static final long serialVersionUID = -569585037138251120L;
	private long version;
	private String notes;
	private String name;
	private boolean deleted;
	private RuleXml rule;
	
	public RuleVersionInfo() {
	}
	
	public RuleVersionInfo(RuleXml xml){
		super();
		setRule(xml);
		this.setName(xml.getName());
		this.setNotes(xml.getNotes());
		this.setVersion(xml.getVersion());
		this.setCreatedBy(xml.getCreatedBy());
		this.setCreatedDate(xml.getCreatedDate());
	}
	
	public RuleXml getRule() {
		return rule;
	}

	public void setRule(RuleXml rule) {
		this.rule = rule;
	}

	public String getRuleId() {
		return getRule().getRuleId();
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
		return getRule().getRuleName();
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}