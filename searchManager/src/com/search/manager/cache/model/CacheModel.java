package com.search.manager.cache.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CacheModel<E> implements Serializable {

	private static final long serialVersionUID = 5653921520358579530L;
	
	public static enum ModelType {
		OBJECT,
		LIST,
		MAP,
		NONE
	};
	
	private E obj;
	private List<E> list;
	private Map<String,E> map;
	// mechanism to detect if object needs to be reloaded (i.e. item already expired because of different day)
	private Date uploadedDate;
	
	public CacheModel() {
		uploadedDate = new Date();
	}
	
	public CacheModel(E obj) {
		this();
		this.obj = obj;
	}

	public CacheModel(List<E> list) {
		this();
		this.list = list;
	}

	public CacheModel(Map<String,E> map) {
		this();
		this.map = map;
	}
	
	public Date getUploadedDate() {
		return uploadedDate;
	}

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
	
	public ModelType getModelType() {
		return (obj != null) ? ModelType.OBJECT : 
			  (list != null) ? ModelType.LIST : 
			   (map != null) ? ModelType.MAP : 
				   			   ModelType.NONE;
	}
	
}
