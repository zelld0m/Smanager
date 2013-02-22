package com.search.manager.report.model.xml;

import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.BooleanUtils;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.enums.RuleEntity;
import com.search.manager.model.RedirectRule;
import com.search.manager.model.RedirectRule.RedirectType;

@XmlRootElement(name = "querycleaning")
@DataTransferObject(converter = BeanConverter.class)
public class RedirectRuleXml extends RuleXml{

	private static final long serialVersionUID = 1L;
	private String description;
	private RedirectType redirectType;
	private String replacementKeyword;
	private String directHit;
	private RuleKeywordXml ruleKeyword;
	private RuleConditionXml ruleCondition;
	private Integer replaceKeywordMessageType;
	private String replaceKeywordMessageCustomText;
	
	public RedirectRuleXml() {
		super(serialVersionUID);
		this.setRuleEntity(RuleEntity.QUERY_CLEANING);
	}

	public RedirectRuleXml(String store, long version, String name, String notes, String username, RedirectRule rr) {
		super(store, name == null ? rr.getRuleName() : name, notes, username);

		if(rr!=null){
			this.setRuleId(rr.getRuleId());
			this.setRuleName(rr.getRuleName());
			this.setDescription(rr.getDescription());
			this.setReplacementKeyword(rr.getChangeKeyword());
			this.setRedirectType(rr.getRedirectType());	
			this.setRuleKeyword(new RuleKeywordXml(rr.getSearchTerms()));
			this.setRuleCondition(new RuleConditionXml(rr.getConditions(), BooleanUtils.isTrue(rr.getIncludeKeyword())));
			this.setCreatedBy(rr.getCreatedBy());
			this.setCreatedDate(rr.getCreatedDate());
			this.setLastModifiedBy(rr.getLastModifiedBy());
			this.setLastModifiedDate(rr.getLastModifiedDate());
			this.setReplacementKeyword(rr.getChangeKeyword());
			this.setReplaceKeywordMessageType(rr.getReplaceKeywordMessageType());
			this.setReplaceKeywordMessageCustomText(rr.getReplaceKeywordMessageCustomText());
		}
		
		setVersion(version);
		setSerial(serialVersionUID);
		this.setCreatedDate(new Date());
	}
	
	public RedirectRuleXml(String store, RedirectRule queryCleaning) {
		this(store, 0, "", "", "", queryCleaning);
	}
	
	@XmlAttribute(name="type")
	public RedirectType getRedirectType() {
		return redirectType;
	}

	public void setRedirectType(RedirectType redirectType) {
		this.redirectType = redirectType;
	}

	@XmlElement(name="replace-kw")
	public String getReplacementKeyword() {
		return replacementKeyword;
	}

	public void setReplacementKeyword(String replacementKeyword) {
		this.replacementKeyword = replacementKeyword;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDirectHit() {
		return directHit;
	}

	public void setDirectHit(String directHit) {
		this.directHit = directHit;
	}

	@XmlElementRef(type=RuleKeywordXml.class)
	public RuleKeywordXml getRuleKeyword() {
		return ruleKeyword;
	}

	public void setRuleKeyword(RuleKeywordXml ruleKeyword) {
		this.ruleKeyword = ruleKeyword;
	}

	@XmlElementRef(type=RuleConditionXml.class)
	public RuleConditionXml getRuleCondition() {
		return ruleCondition;
	}

	public void setRuleCondition(RuleConditionXml ruleCondition) {
		this.ruleCondition = ruleCondition;
	}
	
	@XmlTransient
	public boolean isIncludeKeyword(){
		if(ruleCondition != null)
			return ruleCondition.isIncludeKeyword();
		return false;
	}

	@XmlElement(name="replace-kw-msg-type")
	public Integer getReplaceKeywordMessageType() {
		return replaceKeywordMessageType;
	}

	public void setReplaceKeywordMessageType(Integer replaceKeywordMessageType) {
		this.replaceKeywordMessageType = replaceKeywordMessageType;
	}

	@XmlElement(name="replace-kw-msg-custom-text")
	public String getReplaceKeywordMessageCustomText() {
		return replaceKeywordMessageCustomText;
	}

	public void setReplaceKeywordMessageCustomText(
			String replaceKeywordMessageCustomText) {
		this.replaceKeywordMessageCustomText = replaceKeywordMessageCustomText;
	}
}