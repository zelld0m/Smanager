package com.search.manager.model;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter = BeanConverter.class)
public class SpellRule extends ModelBean {

    private static final long serialVersionUID = 4743020520448226037L;

    private String ruleId;
    private String storeId;
    private String status;

    private String[] searchTerms;
    private String[] suggestions;

    public SpellRule() {
        super();
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
        this.searchTerms = searchTerms;
    }

    public String[] getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(String[] suggestions) {
        this.suggestions = suggestions;
    }
}
