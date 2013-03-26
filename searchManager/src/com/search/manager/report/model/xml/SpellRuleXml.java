package com.search.manager.report.model.xml;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

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
}
