package com.search.manager.core.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchResult<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<T> result;
	private int totalCount;

	public SearchResult() {
		this.result = new ArrayList<T>();
		this.totalCount = -1;
	}

	public SearchResult(List<T> result, int totalCount) {
		this.result = result;
		this.totalCount = totalCount;
	}

	public List<T> getResult() {
		return result;
	}

	public void setResult(List<T> result) {
		this.result = result;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
}
