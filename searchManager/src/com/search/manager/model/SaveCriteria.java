package com.search.manager.model;

import java.io.Serializable;

public class SaveCriteria<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private T model;
	private String username;
	private String comment;
	
	public SaveCriteria(T model, String username, String comment) {
		this.model = model;
		this.username = username;
		this.comment = comment;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}

	public void setModel(T model) {
		this.model = model;
	}

	public T getModel() {
		return model;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getComment() {
		return comment;
	}
	
}
