package com.search.reports.manager.model;

import com.google.common.base.Objects;
import java.util.List;

/**
 *
 * @author Philip Mark Gutierrez
 * @since Oct 10, 2013
 * @version 1.0
 */
public class RuleReport implements java.io.Serializable {
    private static final long serialVersionUID = 3948785062014274292L;

    private List<KeywordReport> keywordReports;

    public RuleReport() {
        // NOTHING
    }

    public RuleReport(List<KeywordReport> keywordReports) {
        this.keywordReports = keywordReports;
    }

    public List<KeywordReport> getKeywordReports() {
        return keywordReports;
    }

    public void setKeywordReports(List<KeywordReport> keywordReports) {
        this.keywordReports = keywordReports;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).
                add("keywordReports", keywordReports).
                toString();
    }
}
