package com.search.manager.core.search;

import java.io.Serializable;
import java.util.List;

import org.apache.poi.ss.formula.functions.T;

public class SearchResult implements Serializable {

	private static final long serialVersionUID = 1L;
	protected List<T> result;
	protected int totalCount = -1;

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
