package com.search.manager.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.DateTime;

import com.search.manager.jodatime.jaxbadapter.DateTimeAdapter;

public class SearchCriteria<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private T model;
	private DateTime startDate;
	private DateTime endDate;
	private Integer pageNumber;
	private Integer itemsPerPage;
	private MatchType matchType;
	private Map<String, Object> additionalCriteria = new HashMap<String, Object>();
	
	public enum ExactMatch {
		MATCH,
		SIMILAR;

		public int getIntValue() {
			switch (this) {
				case MATCH:
				default:
					return 0;
				case SIMILAR:
					return 1;
			}
		}
	}
	
	public enum MatchType {
		MATCH_NAME,
		LIKE_NAME,
		MATCH_ID;
		
		public int getIntValue() {
			switch (this) {
				case LIKE_NAME:
				default:
					return 0;
				case MATCH_NAME:
					return 1;
				case MATCH_ID:
					return 2;
			}
		}
	}
	
	public SearchCriteria(T model, DateTime startDate, DateTime endDate, MatchType matchType, Integer pageNumber, Integer itemsPerPage) {
		this.model = model;
		this.startDate = startDate;
		this.matchType = matchType;
		this.endDate = endDate;
		this.pageNumber = pageNumber;
		this.itemsPerPage = itemsPerPage;
	}
	
	public SearchCriteria(T model, DateTime startDate, DateTime endDate, Integer pageNumber, Integer itemsPerPage) {
		this(model, startDate, endDate, null, pageNumber, itemsPerPage);
	}
	
	public SearchCriteria(T model, MatchType matchType, Integer pageNumber, Integer itemsPerPage) {
		this(model, null, null, matchType, pageNumber, itemsPerPage);
	}
	
	public SearchCriteria(T model, Integer pageNumber, Integer itemsPerPage) {
		this(model, null, pageNumber, itemsPerPage);
	}
	
	public SearchCriteria(T model) {
		this(model, 0, 0);
	}
	
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public DateTime getStartDate() {
		return startDate;
	}

	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	public DateTime getEndDate() {
		return endDate;
	}

	public Integer getStartRow() {
		Integer startRow = null;
		if (pageNumber != null && itemsPerPage != null) {
			if (pageNumber == 0 && itemsPerPage == 0) {
				startRow = 0;
			}
			else {
				startRow = (pageNumber - 1) * itemsPerPage + 1;
			}

		}
		return startRow;
	}

	public Integer getEndRow() {
		Integer endRow = null;
		if (pageNumber != null && itemsPerPage != null) {
			if (pageNumber == 0 && itemsPerPage == 0) {
				endRow = 0;
			}
			else {
				endRow = pageNumber * itemsPerPage;
			}
		}
		return endRow;
	}

	public T getModel() {
		return model;
	}

	public Integer getPageNumber() {
		return pageNumber;
	}

	public Integer getItemsPerPage() {
		return itemsPerPage;
	}

	public void setStartDate(DateTime startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(DateTime endDate) {
		this.endDate = endDate;
	}

	public MatchType getMatchType() {
		return matchType;
	}

	public void setMatchType(MatchType matchType) {
		this.matchType = matchType;
	}

	public Map<String, Object> getAdditionalCriteria() {
		return additionalCriteria;
	}

	public void setAdditionalCriteria(Map<String, Object> additionalCriteria) {
		this.additionalCriteria = additionalCriteria;
	}
}