package com.search.manager.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

import com.search.manager.utility.DateAndTimeUtils;

@DataTransferObject(converter = BeanConverter.class)
public class KeywordStats implements Serializable {

	private static final long serialVersionUID = 736623694133736042L;

	private String keyword;

	private Map<String, Integer> stats;

	public KeywordStats(String keyword) {
		setKeyword(keyword);
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public Map<String, Integer> getStats() {
		return stats;
	}

	public void setStats(Map<String, Integer> stats) {
		this.stats = stats;
	}

	public void addStats(Date date, Integer count) {
		if (stats == null) {
			stats = new HashMap<String, Integer>();
		}

		stats.put(DateAndTimeUtils.getHyphenedDateStringMMDDYYYY(date), count);
	}
}
