package com.search.manager.cache.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class CacheModel<E> implements Serializable {

	private static final long serialVersionUID = 5653921520358579530L;
	
	private List<E> list;
	private Map<String,E> map;

	public List<E> getList() {
		return list;
	}

	public void setList(List<E> list) {
		this.list = list;
	}

	public Map<String, E> getMap() {
		return map;
	}

	public void setMap(Map<String, E> map) {
		this.map = map;
	}
}
