package com.search.webservice.model;

import java.util.List;
import java.util.Map;
import com.search.manager.model.ExcludeResult;

public class ExcludedList extends UserToken{
	private static final long serialVersionUID = -3187359890065079269L;
	private Map<String, List<ExcludeResult>> map;
	List<ExcludeResult> list;
	
	public Map<String, List<ExcludeResult>> getMap() {
		return map;
	}

	public void setMap(Map<String, List<ExcludeResult>> map) {
		this.map = map;
	}

	public List<ExcludeResult> getList() {
		return list;
	}

	public void setList(List<ExcludeResult> list) {
		this.list = list;
	}
}
