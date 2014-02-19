package com.search.manager.core.model;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.core.enums.MemberType;

@DataTransferObject(converter = BeanConverter.class)
public class TypeaheadSuggestion extends ModelBean{

	private static final long serialVersionUID = 1601411811965046795L;

	private String typeaheadSuggestionId;
	private String ruleId;
	private MemberType memberType;
	private String memberValue;
	private Integer sortOrder;
	
	public String getTypeaheadSuggestionId() {
		return typeaheadSuggestionId;
	}
	public void setTypeaheadSuggestionId(String typeaheadSuggestionId) {
		this.typeaheadSuggestionId = typeaheadSuggestionId;
	}
	
	public String getRuleId() {
		return ruleId;
	}
	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}
	
	public MemberType getMemberType() {
		return memberType;
	}
	public void setMemberType(MemberType memberType) {
		this.memberType = memberType;
	}
	
	public String getMemberValue() {
		return memberValue;
	}
	public void setMemberValue(String memberValue) {
		this.memberValue = memberValue;
	}
	
	public Integer getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}
	
}
