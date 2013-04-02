package com.search.manager.report.model.xml;

import java.util.Arrays;
import java.util.Date;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import com.search.manager.model.SpellRule;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;

@XmlRootElement(name = "spellRule")
public class SpellRuleXml extends RuleXml {

    private static final long serialVersionUID = 1L;

    private RuleKeywordXml ruleKeyword;

    private SuggestKeywordXml suggestKeyword;

    private String status;

    public SpellRuleXml() {
        super(serialVersionUID);
    }

    public SpellRuleXml(SpellRule rule) {
        this.setRuleId(rule.getRuleId());
        this.setStore(rule.getStoreId());
        this.setRuleKeyword(new RuleKeywordXml(Arrays.asList(rule.getSearchTerms())));
        this.setSuggestKeyword(new SuggestKeywordXml(Arrays.asList(rule.getSuggestions())));
        this.setCreatedBy(rule.getCreatedBy());
        this.setCreatedDate(rule.getCreatedDate());
        this.setLastModifiedBy(rule.getCreatedBy());
        this.setLastModifiedDate(rule.getLastModifiedDate());
        this.setStatus(rule.getStatus());
    }

    @XmlElementRef(type = RuleKeywordXml.class)
    public RuleKeywordXml getRuleKeyword() {
        return ruleKeyword;
    }

    public void setRuleKeyword(RuleKeywordXml ruleKeyword) {
        this.ruleKeyword = ruleKeyword;
    }

    @XmlElementRef(type = SuggestKeywordXml.class)
    public SuggestKeywordXml getSuggestKeyword() {
        return suggestKeyword;
    }

    public void setSuggestKeyword(SuggestKeywordXml suggestKeyword) {
        this.suggestKeyword = suggestKeyword;
    }

    @XmlAttribute("status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void update(SpellRule rule) {
        this.getRuleKeyword().setKeyword(Arrays.asList(rule.getSearchTerms()));
        this.getSuggestKeyword().setSuggest(Arrays.asList(rule.getSuggestions()));
        this.setLastModifiedBy(rule.getLastModifiedBy());
        this.setLastModifiedDate(rule.getLastModifiedDate());

        if (this.getStatus() == "published") {
            this.setStatus("modified");
        }
        
    }
}
