package com.search.manager.model;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.google.common.base.Function;
import com.search.manager.report.model.xml.SpellRuleXml;
import com.search.manager.utility.StringUtil;

@DataTransferObject(converter = BeanConverter.class)
public class SpellRule extends ModelBean {

    private static final long serialVersionUID = 4743020520448226037L;

    public static final Function<SpellRule, SpellRuleXml> transformer = new Function<SpellRule, SpellRuleXml>() {
        public SpellRuleXml apply(SpellRule rule) {
            return new SpellRuleXml(rule);
        }
    };

    private String ruleId;
    private String storeId;
    private String status;

    private String[] searchTerms;
    private String[] suggestions;

    public SpellRule() {
        super();
    }

    public SpellRule(SpellRuleXml ruleXml) {
        this.ruleId = ruleXml.getRuleId();
        this.status = ruleXml.getStatus();
        this.searchTerms = ruleXml.getRuleKeyword().toArray(new String[ruleXml.getRuleKeyword().size()]);
        this.suggestions = ruleXml.getSuggestKeyword().toArray(new String[ruleXml.getSuggestKeyword().size()]);
        this.createdBy = ruleXml.getCreatedBy();
        this.lastModifiedBy = ruleXml.getLastModifiedBy();
        this.createdDate = ruleXml.getCreatedDate();
        this.lastModifiedDate = ruleXml.getLastModifiedDate();
    }

    public SpellRule(String ruleId, String storeId) {
        this();
        this.ruleId = ruleId;
        this.storeId = storeId;
    }

    public SpellRule(String ruleId, String storeId, String status, String[] searchTerms, String[] suggestions) {
        super();
        this.ruleId = ruleId;
        this.storeId = storeId;
        this.status = status;
        this.searchTerms = searchTerms;
        this.suggestions = suggestions;
    }

    public SpellRule(String ruleId, String storeId, String status, String[] searchTerms, String[] suggestions,
            String createdBy, String lastModifiedBy, Date createdDate, Date lastModifiedDate) {
        super();
        this.ruleId = ruleId;
        this.storeId = storeId;
        this.status = status;
        this.searchTerms = searchTerms;
        this.suggestions = suggestions;
        this.createdBy = createdBy;
        this.lastModifiedBy = lastModifiedBy;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String[] getSearchTerms() {
        return searchTerms;
    }

    public void setSearchTerms(String[] searchTerms) {
        this.searchTerms = StringUtil.toLowerCase(searchTerms);
    }

    public String[] getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(String[] suggestions) {
        this.suggestions = suggestions;
    }

    public String toTabbedSearchTerm() {
        return StringUtils.join(searchTerms, '\t');
    }

    public String toTabbedSuggestions() {
        return StringUtils.join(suggestions, '\t');
    }

    public void fromTabbedSearchTerms(String tabbedSearchTerms) {
        searchTerms = StringUtils.split(tabbedSearchTerms, '\t');
    }

    public void fromTabbedSuggestions(String tabbedSuggestions) {
        suggestions = StringUtils.split(tabbedSuggestions, '\t');
    }

    public boolean sameTermsWith(SpellRule rule) {
        return rule != null && toTabbedSearchTerm().equals(rule.toTabbedSearchTerm())
                && toTabbedSuggestions().equals(rule.toTabbedSuggestions());
    }
}
