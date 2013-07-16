package com.search.manager.report.statistics.fluent;

import com.search.manager.report.statistics.model.BannerStatistics;

/**
 * Builder class for {@link BannerStatistics}
 * 
 * @author Philip Mark Gutierrez
 * @since July 08, 2013
 * @version 0.0.1 
 */
public class BannerStatisticsBuilder {
	private String keyword;
	private String memberId;
	private String imagePath;
	private String linkPath;
	private int clicks;
	private int impressions;

	private BannerStatisticsBuilder(String keyword, String memberId) {
		this.keyword = keyword;
		this.memberId = memberId;
	}

	public BannerStatisticsBuilder keyword(String keyword) {
		this.keyword = keyword;
		return this;
	}

	public BannerStatisticsBuilder memberId(String memberId) {
		this.memberId = memberId;
		return this;
	}
	
	public BannerStatisticsBuilder imagePath(String imagePath) {
		this.imagePath = imagePath;
		return this;
	}
	
	public BannerStatisticsBuilder linkPath(String linkPath) {
		this.linkPath = linkPath;
		return this;
	}
	
	public BannerStatisticsBuilder clicks(int clicks) {
		this.clicks = clicks;
		return this;
	}

	public BannerStatisticsBuilder impressions(int impressions) {
		this.impressions = impressions;
		return this;
	}

	public static BannerStatisticsBuilder create(String keyword, String memberId) {
		return new BannerStatisticsBuilder(keyword, memberId);
	}

	public BannerStatistics build() {
		return new BannerStatistics(keyword, memberId, imagePath, linkPath, clicks,
		        impressions);
	}
}
