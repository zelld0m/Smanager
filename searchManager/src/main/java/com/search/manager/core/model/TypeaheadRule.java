package com.search.manager.core.model;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.report.model.xml.TypeaheadRuleXml;

@DataTransferObject(converter = BeanConverter.class)
public class TypeaheadRule extends ModelBean{

	private static final long serialVersionUID = 7840812498092574024L;

	private String ruleId;
	
	private String storeId;
	private String ruleName;
	
	private Integer sortOrder;

	public TypeaheadRule() {
		super();
	}
	
	public TypeaheadRule(TypeaheadRuleXml xml) {
		super();
		
		this.ruleId = xml.getRuleId();
		this.storeId = xml.getStore();
		this.ruleName = xml.getRuleName();
		this.createdBy = xml.getCreatedBy();
	}
	
	public String getRuleId() {
		return ruleId;
	}
	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}
	public String getStoreId() {
		return storeId;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	
	public String getRuleName() {
		return ruleName;
	}
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}
	
	
}
