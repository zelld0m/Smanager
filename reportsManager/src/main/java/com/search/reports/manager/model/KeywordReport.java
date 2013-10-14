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
public class KeywordReport implements java.io.Serializable {

    private static final long serialVersionUID = -7846144638747029778L;
    private String keyword;
    private List<Report> reports = Lists.newArrayList();

    public KeywordReport() {
        // NOTHING
    }

    public KeywordReport(String keyword, List<Report> reports) {
        this.keyword = keyword;
        this.reports = reports;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }
    
    public void addReport(Report report) {
        reports.add(report);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).
                add("keyword", keyword).
                add("reports", reports).
                toString();
    }
}
