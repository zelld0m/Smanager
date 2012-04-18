package com.search.webservice.model;

import java.util.List;
import java.util.Map;

import com.search.manager.model.ElevateResult;

public class ElevatedList extends UserToken{
	private static final long serialVersionUID = -3187359890065079269L;
	private Map<String, List<ElevateResult>> map;
	List<ElevateResult> list;
	
	public Map<String, List<ElevateResult>> getMap() {
		return map;
	}

	public void setMap(Map<String, List<ElevateResult>> map) {
		this.map = map;
	}

	public List<ElevateResult> getList() {
		return list;
	}

	public void setList(List<ElevateResult> list) {
		this.list = list;
	}
}
