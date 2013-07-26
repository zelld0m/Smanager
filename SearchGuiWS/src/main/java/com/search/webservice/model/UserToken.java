package com.search.webservice.model;

import java.io.Serializable;

public class UserToken implements Serializable {
	private static final long serialVersionUID = -5346657605363690319L;

	private String token;
	private String store;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getStore() {
		return store;
	}

	public void setStore(String store) {
		this.store = store;
	}

}