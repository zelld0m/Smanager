package com.search.manager.model;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.enums.RuleType;

@DataTransferObject(converter = BeanConverter.class)
public class ExportRuleMap {
	
	private static final long serialVersionUID = 1L;
	
	private String 		storeIdOrigin;
	private String 		ruleIdOrigin;
	private String 		ruleNameOrigin;
	private String 		storeIdTarget;
	private String 		ruleIdTarget;
	private String 		ruleNameTarget;
	private RuleType 	ruleType;
	
	public ExportRuleMap(String storeIdOrigin, String ruleIdOrigin, String ruleNameOrigin, String storeIdTarget, String ruleIdTarget, String ruleNameTarget) {
		super();
		this.storeIdOrigin = storeIdOrigin;
		this.ruleIdOrigin = ruleIdOrigin;
		this.ruleNameOrigin = ruleNameOrigin;
		this.storeIdTarget = storeIdTarget;
		this.ruleIdTarget = ruleIdTarget;
		this.ruleNameTarget = ruleNameTarget;
	}
	
	public ExportRuleMap(String storeIdOrigin, String ruleIdOrigin, String ruleNameOrigin, String storeIdTarget, String ruleIdTarget, String ruleNameTarget, RuleType ruleType) {
		this(storeIdOrigin, ruleIdOrigin, ruleNameOrigin, storeIdTarget, ruleIdTarget, ruleNameTarget);
		this.ruleType= ruleType;
	}
	
	public ExportRuleMap(String storeIdOrigin, String ruleIdOrigin, String ruleNameOrigin, String storeIdTarget, String ruleIdTarget, String ruleNameTarget, Integer ruleType) {
		this(storeIdOrigin, ruleIdOrigin, ruleNameOrigin, storeIdTarget, ruleIdTarget, ruleNameTarget);
		if (ruleType != null) {
			RuleType ruleTypeObject = RuleType.get(ruleType);
			if (ruleTypeObject != null) {
				this.ruleType = ruleTypeObject;
			}			
		}
	}
	
	public String getStoreIdOrigin() {
		return storeIdOrigin;
	}
	
	public void setStoreIdOrigin(String storeIdOrigin) {
		this.storeIdOrigin = storeIdOrigin;
	}
	
	public String getRuleIdOrigin() {
		return ruleIdOrigin;
	}
	
	public void setRuleIdOrigin(String ruleIdOrigin) {
		this.ruleIdOrigin = ruleIdOrigin;
	}
	
	public String getRuleNameOrigin() {
		return ruleNameOrigin;
	}
	
	public void setRuleNameOrigin(String ruleNameOrigin) {
		this.ruleNameOrigin = ruleNameOrigin;
	}
	
	public String getStoreIdTarget() {
		return storeIdTarget;
	}
	
	public void setStoreIdTarget(String storeIdTarget) {
		this.storeIdTarget = storeIdTarget;
	}
	
	public String getRuleIdTarget() {
		return ruleIdTarget;
	}
	
	public void setRuleIdTarget(String ruleIdTarget) {
		this.ruleIdTarget = ruleIdTarget;
	}
	
	public String getRuleNameTarget() {
		return ruleNameTarget;
	}
	
	public void setRuleNameTarget(String ruleNameTarget) {
		this.ruleNameTarget = ruleNameTarget;
	}
	
	public RuleType getRuleType() {
		return ruleType;
	}
	
	public void setRuleType(RuleType ruleType) {
		this.ruleType = ruleType;
	}
	
}
