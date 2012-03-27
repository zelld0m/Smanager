package com.search.manager.model;

import java.io.Serializable;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.annotations.RemoteProperty;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter = BeanConverter.class)
public class Store implements Serializable {

	private static final long serialVersionUID = 2184076713975341261L;

	private String storeId;
	private String storeName;
	
	public Store(String storeId, String storeName) {
		this.storeId = storeId;
		this.storeName = storeName;
	}
	
	public Store(String storeId) {
		this.storeId = storeId;
		this.storeName = storeId;
	}
	
	@RemoteProperty
	public String getStoreId() {
		return storeId;
	}
	
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	
	@RemoteProperty
	public String getStoreName() {
		return storeName;
	}
	
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	
	@Override
	public String toString() {
		return "(Store Id: " + storeId + "\tStore Name: " + storeName + ")";
	}

}
