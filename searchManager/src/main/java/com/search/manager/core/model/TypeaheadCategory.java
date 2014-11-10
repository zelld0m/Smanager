package com.search.manager.core.model;

public class TypeaheadCategory extends ModelBean{

	private static final long serialVersionUID = 8923795027648854570L;

	private String typeaheadCategoryId;
	private String ruleId;
	private String category;
	
	
	public String getTypeaheadCategoryId() {
		return typeaheadCategoryId;
	}
	public void setTypeaheadCategoryId(String typeaheadCategoryId) {
		this.typeaheadCategoryId = typeaheadCategoryId;
	}
	public String getRuleId() {
		return ruleId;
	}
	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
	
}
