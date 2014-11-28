package com.search.manager.core.model;

import java.beans.Transient;

import com.search.manager.core.enums.SectionType;

public class TypeaheadSection extends ModelBean{

	private static final long serialVersionUID = -7044509686061209215L;
		
	private String typeaheadSectionId;
	private String ruleId;
	private String name;
	private int type;
	private transient SectionType sectionType;
	private boolean disabled;
	
	public boolean isDisabled() {
		return disabled;
	}
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	public String getTypeaheadSectionId() {
		return typeaheadSectionId;
	}
	public void setTypeaheadSectionId(String typeaheadSectionId) {
		this.typeaheadSectionId = typeaheadSectionId;
	}
	public String getRuleId() {
		return ruleId;
	}
	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	@Transient
	public SectionType getSectionType() {
		sectionType = SectionType.values()[type];
		return sectionType;
	}
		
}
