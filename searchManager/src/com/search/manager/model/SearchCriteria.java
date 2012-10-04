package com.search.manager.model;

import java.io.Serializable;
import java.util.Date;

public class SearchCriteria<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private T model;
	private Date startDate;
	private Date endDate;
	private Integer pageNumber;
	private Integer itemsPerPage;
	
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
	
	public SearchCriteria(T model, Date startDate, Date endDate, Integer pageNumber, Integer itemsPerPage) {
		this.model = model;
		this.startDate = startDate;
		this.endDate = endDate;
		this.pageNumber = pageNumber;
		this.itemsPerPage = itemsPerPage;
	}
	
	public SearchCriteria(T model, Integer pageNumber, Integer itemsPerPage) {
		this(model, null, null, pageNumber, itemsPerPage);
	}
	
	public SearchCriteria(T model) {
		this(model, null, null, 0, 0);
	}
	
	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
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

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
