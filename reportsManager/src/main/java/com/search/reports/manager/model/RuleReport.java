package com.search.reports.manager.model;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import java.util.List;

/**
 *
 * @author Philip Mark Gutierrez
 * @since Oct 10, 2013
 * @version 1.0
 */
public class RuleReport implements java.io.Serializable {

    private static final long serialVersionUID = 3948785062014274292L;
    private String ruleName;
    private List<KeywordReport> keywordReports = Lists.newArrayList();

    public RuleReport() {
        // NOTHING
    }

    public RuleReport(String ruleName, List<KeywordReport> keywordReports) {
        this.ruleName = ruleName;
        this.keywordReports = keywordReports;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public List<KeywordReport> getKeywordReports() {
        return keywordReports;
    }

    public void setKeywordReports(List<KeywordReport> keywordReports) {
        this.keywordReports = keywordReports;
    }

    public void addKeywordReport(KeywordReport keywordReport) {
        keywordReports.add(keywordReport);
    }
    
    @Override
    public String toString() {
        return Objects.toStringHelper(this).
                add("ruleName", ruleName).
                add("keywordReports", keywordReports).
                toString();
    }
}
