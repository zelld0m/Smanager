package com.search.manager.model;

import java.io.Serializable;
import java.util.List;

public class Redirect implements Serializable {
	private List<StoreKeyword> storeKeyword;
	private String parameters;
	
	public List<StoreKeyword> getStoreKeyword() {
		return storeKeyword;
	}
	public void setStoreKeyword(List<StoreKeyword> storeKeyword) {
		this.storeKeyword = storeKeyword;
	}
	public String getParameters() {
		return parameters;
	}
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
}
