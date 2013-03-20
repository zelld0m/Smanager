package com.search.manager.model;

import java.io.Serializable;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.annotations.RemoteProperty;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter = BeanConverter.class)
public class Store implements Serializable {

	private static final long serialVersionUID = 2184076713975341261L;

	private String storeId;
	
	public Store(String storeId) {
		this.storeId = storeId;
	}
	
	public Store() {}
	
	@RemoteProperty
	public String getStoreId() {
		return storeId;
	}
	
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	
	@Override
	public String toString() {
		return getStoreId();
	}
}