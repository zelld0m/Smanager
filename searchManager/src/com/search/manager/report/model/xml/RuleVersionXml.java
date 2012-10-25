package com.search.manager.report.model.xml;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

@XmlSeeAlso({
	ElevateRuleXml.class,
	ExcludeRuleXml.class,
	DemoteRuleXml.class
})
public class RuleVersionXml implements Serializable {
	
	private static final long serialVersionUID = -368623910806297877L;
	
	private String store;
	private String ruleId;
	private String ruleName;
	private long version;
	private String notes;
	private String name;
	private String createdBy;
	private Date createdDate;
	private long serial;
	
	public RuleVersionXml() {
		super();
	}

	public RuleVersionXml(long serial) {
		super();
		this.serial = serial;
	}
	
	public RuleVersionXml(String store, String name, String notes,
			String createdBy) {
		super();
		this.store = store;
		this.notes = notes;
		this.name = name;
		this.createdBy = createdBy;
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
	
	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	@XmlTransient
	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	@XmlTransient
	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	@XmlAttribute
	public long getSerial() {
		return serial;
	}

	public void setSerial(long serial) {
		this.serial = serial;
	}

	@XmlAttribute
	public void setVersion(long version) {
		this.version = version;
	}
	
	public void setStore(String store) {
		this.store = store;
	}

	@XmlAttribute
	public String getStore() {
		return store;
	}
	
	public long getVersion() {
		return version;
	}
}