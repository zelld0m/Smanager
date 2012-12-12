package com.search.manager.report.model.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.collections.CollectionUtils;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.model.RedirectRuleCondition;

@XmlRootElement(name="conditions")
@XmlType(propOrder={"includeKeyword","condition"})
@XmlSeeAlso({
	RedirectRuleCondition.class
})
@DataTransferObject(converter = BeanConverter.class)
public class RuleConditionXml extends BaseEntityXml{

	private static final long serialVersionUID = -7421229784911639782L;
	private boolean includeKeyword;
	private List<String> condition;
	private List<RedirectRuleCondition> ruleConditionList;

	public RuleConditionXml() {
		super();
	}

	public RuleConditionXml(List<String> condition, boolean includeKeyword) {
		super();
		this.includeKeyword = includeKeyword;
		this.setCondition(condition);
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
		
		List<RedirectRuleCondition> ruleCondition = new ArrayList<RedirectRuleCondition>();
		
		if(CollectionUtils.isNotEmpty(condition)){
			for(String condSolr: condition){
				ruleCondition.add(new RedirectRuleCondition(condSolr));
			}
		}
		
		this.setRuleCondition(ruleCondition);
	}      

	@XmlTransient
	public List<RedirectRuleCondition> getRuleCondition() {		return ruleConditionList;	}	public void setRuleCondition(List<RedirectRuleCondition> ruleConditionList) {		this.ruleConditionList = ruleConditionList;	}}