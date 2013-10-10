package com.search.reports.manager.model.builder;

import com.search.reports.manager.model.KeywordReport;
import java.util.Date;

/**
 *
 * @author Philip Mark Gutierrez
 * @since Oct 10, 2013
 * @version 1.0
 */
public class KeywordReportBuilder {

    private String ruleName;
    private String keyword;
    private String rank;
    private String sku;
    private String name;
    private Date expiration;
    
    private KeywordReportBuilder(String ruleName, String keyword) {
        this.ruleName = ruleName;
        this.keyword = keyword;
    }
    
    public KeywordReportBuilder rank(String rank) {
        this.rank = rank;
        return this;
    }
    
    public KeywordReportBuilder sku(String sku) {
        this.sku = sku;
        return this;
    }
    
    public KeywordReportBuilder name(String name) {
        this.name = name;
        return this;
    }
    
    public KeywordReportBuilder expiration(Date expiration) {
        this.expiration = expiration;
        return this;
    }
    
    public static KeywordReportBuilder create(String ruleName, String keyword) {
        return new KeywordReportBuilder(ruleName, keyword);
    }
    
    public KeywordReport build() {
        return new KeywordReport(ruleName, keyword, rank, sku, name, expiration);
    }
}
