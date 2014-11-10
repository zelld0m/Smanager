package com.search.manager.core.model;

public class TypeaheadSectionItem extends ModelBean{

	private static final long serialVersionUID = -840379859172065115L;
	
	private String typeaheadSectionItemId;
	private String ruleId;
	private String sectionId;
	private String value;
	
	public String getTypeaheadSectionItemId() {
		return typeaheadSectionItemId;
	}
	public void setTypeaheadSectionItemId(String typeaheadSectionItemId) {
		this.typeaheadSectionItemId = typeaheadSectionItemId;
	}
	public String getRuleId() {
		return ruleId;
	}
	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}
	public String getSectionId() {
		return sectionId;
	}
	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
}
