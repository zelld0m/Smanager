package com.search.manager.report.model.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import com.search.manager.enums.RuleEntity;
import com.search.manager.model.Product;
import com.search.manager.model.RuleStatus;

@XmlSeeAlso({
	ElevateRuleXml.class,
	ExcludeRuleXml.class,
	DemoteRuleXml.class,
	FacetSortRuleXml.class,
	RedirectRuleXml.class,
	RankingRuleXml.class,
	RuleStatus.class,
	RuleEntity.class,
	Product.class
})
public class RuleXml extends BaseEntityXml{
	
	private static final long serialVersionUID = -368623910806297877L;
	
	private String store;
	private String ruleId;
	private String ruleName;
	private RuleEntity ruleEntity;
	private long version;
	private String notes;
	private String name;
	private long serial;
	private boolean deleted;
	private RuleStatus ruleStatus;
	
	public RuleXml() {
		super();
	}

	public RuleXml(long serial) {
		super();
		this.serial = serial;
		this.ruleEntity = getRuleEntity(this);
	}
	
	public RuleXml(String store, String name, String notes,
			String createdBy) {
		super();
		this.store = store;
		this.notes = notes;
		this.name = name;
		setCreatedBy(createdBy);
		this.ruleEntity = getRuleEntity(this);
	}

	public RuleStatus getRuleStatus() {
		return ruleStatus;
	}

	public void setRuleStatus(RuleStatus ruleStatus) {
		this.ruleStatus = ruleStatus;
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

	@XmlTransient
	public RuleEntity getRuleEntity() {
		return ruleEntity;
	}

	public static RuleEntity getRuleEntity(RuleXml ruleXml) {
		RuleEntity ruleEntity = null;
		if (ruleXml == null) {
		}
		else if (ruleXml instanceof ElevateRuleXml){
			ruleEntity = RuleEntity.ELEVATE;
		}
		else if(ruleXml instanceof DemoteRuleXml){
			ruleEntity = RuleEntity.DEMOTE;
		}
		else if(ruleXml instanceof ExcludeRuleXml){
			ruleEntity = RuleEntity.EXCLUDE;
		}
		else if(ruleXml instanceof FacetSortRuleXml){
			ruleEntity = RuleEntity.FACET_SORT;
		}
		else if(ruleXml instanceof RedirectRuleXml){
			ruleEntity = RuleEntity.QUERY_CLEANING;
		}
		else if(ruleXml instanceof RankingRuleXml){
			ruleEntity = RuleEntity.RANKING_RULE;
		}
		return ruleEntity;
	}
	
	public void setRuleEntity(RuleEntity ruleEntity) {
		this.ruleEntity = ruleEntity;
	}
}