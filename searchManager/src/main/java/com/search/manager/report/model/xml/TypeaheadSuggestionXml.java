package com.search.manager.report.model.xml;

import javax.xml.bind.annotation.XmlRootElement;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.core.enums.MemberType;
import com.search.manager.core.model.TypeaheadSuggestion;

@XmlRootElement(name = "typeaheadSuggestionXml")
@DataTransferObject(converter = BeanConverter.class)
public class TypeaheadSuggestionXml extends RuleItemXml{

	private static final long serialVersionUID = 1L;

	private MemberType typeaheadMemberType;
	private String memberValue;
	private Integer sortOrder;
	
	public TypeaheadSuggestionXml() {
		super();
	}
	
	public TypeaheadSuggestionXml(TypeaheadSuggestion suggestion) {
		super();
		this.typeaheadMemberType = suggestion.getMemberType();
		this.memberValue = suggestion.getMemberValue();
		this.sortOrder = suggestion.getSortOrder();
	}
	
	public MemberType getTypeaheadMemberType() {
		return typeaheadMemberType;
	}
	public void setTypeaheadMemberType(MemberType typeaheadMemberType) {
		this.typeaheadMemberType = typeaheadMemberType;
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
