package com.search.reports.manager.model;

import com.google.common.base.Objects;
import java.util.Date;

/**
 *
 * @author Philip Mark Gutierrez
 * @since Oct 11, 2013
 * @version 1.0
 */
public class Report implements java.io.Serializable {

    private static final long serialVersionUID = -1021655514519717216L;
    private String rank;
    private String sku;
    private String name;
    private Date expiration;

    public Report() {
    }

    public Report(String rank, String sku, String name, Date expiration) {
        this.rank = rank;
        this.sku = sku;
        this.name = name;
        this.expiration = expiration;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).
                add("rank", rank).
                add("sku", sku).
                add("name", name).
                add("expiration", expiration).
                toString();
    }
}
