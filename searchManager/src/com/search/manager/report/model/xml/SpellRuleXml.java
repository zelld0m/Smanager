package com.search.manager.report.model.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.google.common.base.Function;
import com.search.manager.model.ModelBean;
import com.search.manager.model.SpellRule;

@XmlRootElement(name = "spellRule")
@DataTransferObject(converter = BeanConverter.class)
public class SpellRuleXml extends ModelBean {

    private static final long serialVersionUID = 1L;

    public static final Function<SpellRuleXml, SpellRule> transformer = new Function<SpellRuleXml, SpellRule>() {
        public SpellRule apply(SpellRuleXml xml) {
            return new SpellRule(xml);
        }
    };

    private byte[] ruleId;

    private byte[][] ruleKeyword;

    private byte[][] suggestKeyword;

    private byte[] status;

    private List<String> ruleKeywordList;

    private List<String> suggestKeywordList;

    public SpellRuleXml() {
    }

    public SpellRuleXml(SpellRule rule) {
        this.setRuleId(rule.getRuleId());
        this.setRuleKeyword(Arrays.asList(rule.getSearchTerms()));
        this.setSuggestKeyword(Arrays.asList(rule.getSuggestions()));
        this.setCreatedBy(rule.getCreatedBy());
        this.setCreatedDate(rule.getCreatedDate());
        this.setLastModifiedBy(rule.getCreatedBy());
        this.setLastModifiedDate(rule.getLastModifiedDate());
        this.setStatus(rule.getStatus());
    }

    @XmlElement(name = "keyword")
    public List<String> getRuleKeyword() {
        if (ruleKeywordList != null) {
            setRuleKeyword(ruleKeywordList);
            ruleKeywordList.clear();
            ruleKeywordList = null;
        }

        if (ruleKeyword != null) {
            List<String> keywords = new ArrayList<String>();
            for (int i = 0; i < ruleKeyword.length; i++) {
                keywords.add(new String(ruleKeyword[i]));
            }
            return keywords;
        } else {
            ruleKeywordList = new ArrayList<String>();
            return ruleKeywordList;
        }
    }

    public void setRuleKeyword(List<String> ruleKeyword) {
        if (ruleKeyword != null) {
            this.ruleKeyword = new byte[ruleKeyword.size()][];
            int i = 0;
            for (String kw : ruleKeyword) {
                this.ruleKeyword[i] = kw.getBytes();
                i++;
            }
        }
    }

    @XmlElement(name = "suggest")
    public List<String> getSuggestKeyword() {
        if (suggestKeywordList != null) {
            setSuggestKeyword(suggestKeywordList);
            suggestKeywordList.clear();
            suggestKeywordList = null;
        }

        if (suggestKeyword != null) {
            List<String> keywords = new ArrayList<String>();

            for (int i = 0; i < suggestKeyword.length; i++) {
                keywords.add(new String(suggestKeyword[i]));
            }

            return keywords;
        } else {
            suggestKeywordList = new ArrayList<String>();
            return suggestKeywordList;
        }
    }

    public void setSuggestKeyword(List<String> suggestKeyword) {
        if (suggestKeyword != null) {
            this.suggestKeyword = new byte[suggestKeyword.size()][];
            int i = 0;
            for (String kw : suggestKeyword) {
                this.suggestKeyword[i] = kw.getBytes();
                i++;
            }
        }
    }

    @XmlAttribute(name = "status")
    public String getStatus() {
        return status != null ? new String(status) : null;
    }

    public void setStatus(String status) {
        if (status != null)
            this.status = status.getBytes();
    }

    public void update(SpellRule rule) {
        this.setRuleKeyword(Arrays.asList(rule.getSearchTerms()));
        this.setSuggestKeyword(Arrays.asList(rule.getSuggestions()));
        this.setLastModifiedBy(rule.getLastModifiedBy());
        this.setLastModifiedDate(rule.getLastModifiedDate());

        if (status != null && "published".equalsIgnoreCase(new String(this.status))) {
            this.setStatus("modified");
        }
    }

    public String getRuleId() {
        return ruleId != null ? new String(ruleId) : null;
    }

    public void setRuleId(String ruleId) {
        if (ruleId != null)
            this.ruleId = ruleId.getBytes();
    }
}
