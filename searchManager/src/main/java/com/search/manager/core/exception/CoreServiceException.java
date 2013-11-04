package com.search.manager.core.exception;

public class CoreServiceException extends Exception {
	private static final long serialVersionUID = 1L;

	public CoreServiceException() {
		super();
	}

	public CoreServiceException(String desc) {
		super(desc);
	}

	public CoreServiceException(Throwable cause) {
		super(cause);
	}

	public CoreServiceException(String desc, Throwable cause) {
		super(desc, cause);
	}

}
