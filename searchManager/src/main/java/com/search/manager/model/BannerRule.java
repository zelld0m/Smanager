package com.search.manager.model;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.report.model.xml.BannerRuleXml;

@DataTransferObject(converter = BeanConverter.class)
@Deprecated
public class BannerRule extends ModelBean {

	private static final long serialVersionUID = 1L;

	private String storeId;
	private String ruleId;
	private String ruleName;
	
	public BannerRule() {
		super();
	}
	
	public BannerRule(String storeId, String ruleId, String ruleName, String createdBy, String lastModifiedBy) {
		super();
		this.storeId = storeId;
		this.ruleId = ruleId;
		this.ruleName = ruleName;
		this.createdBy = createdBy;
		this.lastModifiedBy = lastModifiedBy;
	}
	
	public BannerRule(String storeId, String ruleId, String ruleName, String createdBy) {
		this(storeId, ruleId, ruleName, createdBy, null);
	}
	
	public BannerRule(String storeId, String ruleName, String createdBy) {
		this(storeId, null, ruleName, createdBy);
	}

	public BannerRule(String storeId, String ruleName) {
		this(storeId, null, ruleName, null, null);
	}
	
	public BannerRule(String storeId) {
		this(storeId, null, null, null, null);
	}

	public BannerRule(BannerRuleXml xml) {
	    this(xml.getStore(), xml.getRuleId(), xml.getRuleName(), xml.getCreatedBy());
    }

    public String getStoreId() {
		return storeId;
	}
	
	public void setStoreId(String storeId) {
		this.storeId = storeId;
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
}