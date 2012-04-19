package com.search.manager.cache.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

public class CacheModel<E> implements Serializable {

	private static final long serialVersionUID = 5653921520358579530L;
	
	private E obj;
	private List<E> list;
	private Map<String,E> map;

	public E getObj() {
		return obj;
	}

	public void setObj(E obj) {
		this.obj = obj;
	}

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
