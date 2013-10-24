package com.search.manager.core.exception;

public class CoreDaoException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public CoreDaoException() {
		super();
	}
	
	public CoreDaoException(String desc) {
		super(desc);
	}
	
	public CoreDaoException(Throwable cause) {
		super(cause);
	}
	
	public CoreDaoException(String desc, Throwable cause) {
		super(desc, cause);
	}
	
}
