package com.search.manager.dao;

public class DaoException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public DaoException(String desc) {
		super(desc);
	}
	
	public DaoException(String desc, Throwable cause) {
		super(desc, cause);
	}

}
