package com.search.manager.report.model.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

@XmlSeeAlso({
	ElevateRuleXml.class,
	ExcludeRuleXml.class,
	DemoteRuleXml.class,
	FacetSortRuleXml.class,
	RedirectRuleXml.class,
	RankingRuleXml.class,
})
public class RuleVersionXml extends BaseEntityXml{
	
	private static final long serialVersionUID = -368623910806297877L;
	
	private String store;
	private String ruleId;
	private String ruleName;
	private long version;
	private String notes;
	private String name;
	private long serial;
	private boolean deleted;
	
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
		setCreatedBy(createdBy);
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

	@XmlAttribute
	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}	
}