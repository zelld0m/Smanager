package com.search.manager.report.model.xml;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.search.manager.model.RedirectRule;

@XmlRootElement(name = "querycleaning")
public class RedirectRuleXml extends RuleVersionXml{

	private static final long serialVersionUID = 5605017143398572331L;
	private String description;
	private String redirectType;
	private String condition;
	private String changeKeyword;
	private List<String> ruleKeyword;
	
	public RedirectRuleXml() {
		super(serialVersionUID);
	}

	public RedirectRuleXml(String store, long version, String name, String notes, String username,
			RedirectRule rr) {
		super(store, name, notes, username);

		if(rr!=null){
			this.setRuleId(rr.getRuleId());
			this.setRuleName(rr.getRuleName());
			this.ruleKeyword = rr.getSearchTerms();
		}
		
		setVersion(version);
		setSerial(serialVersionUID);
		this.setCreatedDate(new Date());
	}
	
	public String getRedirectType() {
		return redirectType;
	}

	public void setRedirectType(String redirectType) {
		this.redirectType = redirectType;
	}


	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getChangeKeyword() {
		return changeKeyword;
	}

	public void setChangeKeyword(String changeKeyword) {
		this.changeKeyword = changeKeyword;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getRuleKeyword() {
		return ruleKeyword;
	}

	public void setRuleKeyword(List<String> ruleKeyword) {
		this.ruleKeyword = ruleKeyword;
	}
}