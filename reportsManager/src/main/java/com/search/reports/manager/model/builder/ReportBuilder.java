package com.search.reports.manager.model.builder;

import com.search.reports.manager.model.Report;
import java.util.Date;

/**
 *
 * @author Philip Mark Gutierrez
 * @since Oct 10, 2013
 * @version 1.0
 */
public class ReportBuilder {

    private String rank;
    private String sku;
    private String name;
    private Date expiration;
    
    private ReportBuilder() {
    }
    
    public ReportBuilder rank(String rank) {
        this.rank = rank;
        return this;
    }
    
    public ReportBuilder sku(String sku) {
        this.sku = sku;
        return this;
    }
    
    public ReportBuilder name(String name) {
        this.name = name;
        return this;
    }
    
    public ReportBuilder expiration(Date expiration) {
        this.expiration = expiration;
        return this;
    }
    
    public static ReportBuilder create() {
        return new ReportBuilder();
    }
    
    public Report build() {
        return new Report(rank, sku, name, expiration);
    }
}
