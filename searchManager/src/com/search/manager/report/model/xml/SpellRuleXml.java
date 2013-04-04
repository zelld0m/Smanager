package com.search.manager.report.model.xml;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Function;
import com.search.manager.model.ModelBean;
import com.search.manager.model.SpellRule;

@XmlRootElement(name = "spellRule")
public class SpellRuleXml extends ModelBean {

    private static final long serialVersionUID = 1L;
    
    public static final Function<SpellRuleXml, SpellRule> transformer = new Function<SpellRuleXml, SpellRule>() {
        public SpellRule apply(SpellRuleXml xml) {
            return new SpellRule(xml);
        }
    };

    private String ruleId;
    
    private RuleKeywordXml ruleKeyword;

    private SuggestKeywordXml suggestKeyword;

    private String status;

    public SpellRuleXml() {
    }

    public SpellRuleXml(SpellRule rule) {
        this.setRuleId(rule.getRuleId());
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

    @XmlAttribute(name = "status")
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

        if ("published".equalsIgnoreCase(this.status)) {
            this.setStatus("modified");
        }
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }
}
