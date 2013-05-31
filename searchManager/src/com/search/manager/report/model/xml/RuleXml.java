package com.search.manager.report.model.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

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
	Product.class,
	BannerRuleXml.class,
	SpellRules.class
})
@DataTransferObject(converter = BeanConverter.class)
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
	private boolean rejected;
	private RuleStatus ruleStatus;
	private boolean fileExists;
	
	public RuleXml() {
		super();
	}

	public RuleXml(long serial) {
		super();
		this.serial = serial;
	}
	
	public RuleXml(String store, String name, String notes,
			String createdBy) {
		super();
		this.store = store;
		this.notes = notes;
		this.name = name;
		setCreatedBy(createdBy);
	}

	public RuleXml(String store, String ruleId, String ruleName, boolean deleted, boolean rejected) {
		this.store = store;
		this.ruleName = ruleName;
		this.ruleId = ruleId;
		this.deleted = deleted;
		this.rejected = rejected;
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
	public boolean isRejected() {
		return rejected;
	}

	public void setRejected(boolean rejected) {
		this.rejected = rejected;
	}

	@XmlTransient
	public RuleEntity getRuleEntity() {
		return ruleEntity;
	}
	
	public void setRuleEntity(RuleEntity ruleEntity) {
		this.ruleEntity = ruleEntity;
	}

	@XmlTransient
	public boolean isFileExists() {
		return fileExists;
	}

	public void setFileExists(boolean fileExists) {
		this.fileExists = fileExists;
	}
	
}
