package com.search.manager.report.statistics.model;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

/**
 * POJO class for Statistics
 *
 * @author Philip Mark Gutierrez
 * @since July 08, 2013
 * @version 0.0.1
 */
@DataTransferObject(converter = BeanConverter.class)
public class BannerStatistics {

    private String keyword;
    private String memberId;
    private String imagePath;
    private String linkPath;
    private int clicks;
    private int impressions;

    public BannerStatistics() {
        // NOTHING!
    }

    public BannerStatistics(String keyword, String memberId) {
        this.keyword = keyword;
        this.memberId = memberId;
    }

    public BannerStatistics(String keyword, String memberId, String imagePath,
            String linkPath, int clicks, int impressions) {
        this(keyword, memberId);
        this.imagePath = imagePath;
        this.linkPath = linkPath;
        this.clicks = clicks;
        this.impressions = impressions;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getLinkPath() {
        return linkPath;
    }

    public void setLinkPath(String linkPath) {
        this.linkPath = linkPath;
    }

    public int getClicks() {
        return clicks;
    }

    public void setClicks(int clicks) {
        this.clicks = clicks;
    }

    public int getImpressions() {
        return impressions;
    }

    public void setImpressions(int impressions) {
        this.impressions = impressions;
    }

    public void incrementClicks(int incrementBy) {
        this.clicks += incrementBy;
    }

    public void incrementImpressions(int incrementBy) {
        this.impressions += incrementBy;
    }

    @Override
    public String toString() {
        return "BannerStatistics{" + "keyword=" + keyword + ", memberId="
                + memberId + ", imagePath=" + imagePath + ", linkPath="
                + linkPath + ", clicks=" + clicks + ", impressions="
                + impressions + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.keyword != null ? this.keyword.hashCode() : 0);
        hash = 59 * hash + (this.memberId != null ? this.memberId.hashCode() : 0);
        hash = 59 * hash + (this.imagePath != null ? this.imagePath.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BannerStatistics other = (BannerStatistics) obj;
        if ((this.keyword == null) ? (other.keyword != null) : !this.keyword.equals(other.keyword)) {
            return false;
        }
        if ((this.memberId == null) ? (other.memberId != null) : !this.memberId.equals(other.memberId)) {
            return false;
        }
        if ((this.imagePath == null) ? (other.imagePath != null) : !this.imagePath.equals(other.imagePath)) {
            return false;
        }
        return true;
    }
}
