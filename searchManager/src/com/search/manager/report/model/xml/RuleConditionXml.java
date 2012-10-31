package com.search.manager.report.model.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="conditions")
@XmlType(propOrder={"includeKeyword","condition"})
public class RuleConditionXml extends BaseEntityXml{
	
	private static final long serialVersionUID = -7421229784911639782L;
	private boolean includeKeyword;
	private List<String> condition;
	
	public RuleConditionXml() {
		super();
	}
	
	public RuleConditionXml(List<String> condition, boolean includeKeyword) {
		super();
		this.includeKeyword = includeKeyword;
		this.condition = condition;
	}

	public RuleConditionXml(List<String> condition) {
		this(condition, false);
	}
	
	public boolean isIncludeKeyword() {
		return includeKeyword;
	}

	public void setIncludeKeyword(boolean includeKeyword) {
		this.includeKeyword = includeKeyword;
	}

	public List<String> getCondition() {
		return condition;
	}

	public void setCondition(List<String> condition) {
		this.condition = condition;
	}
}