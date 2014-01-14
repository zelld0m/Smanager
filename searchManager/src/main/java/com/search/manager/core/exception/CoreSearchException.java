package com.search.manager.core.exception;

public class CoreSearchException extends Exception {

	private static final long serialVersionUID = 1L;

	public CoreSearchException() {
		super();
	}

	public CoreSearchException(String desc) {
		super(desc);
	}

	public CoreSearchException(Throwable cause) {
		super(cause);
	}

	public CoreSearchException(String desc, Throwable cause) {
		super(desc, cause);
	}

}
