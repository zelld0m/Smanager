package com.search.manager.exception;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.ExceptionConverter;

@DataTransferObject(converter = ExceptionConverter.class)
public class PublishLockException extends Exception {

	private static final long serialVersionUID = 1;
	
	private String username;
	private String storeLabel;
	
	public PublishLockException(String message, String username, String storeLabel) {
		super(message);
		this.username = username;
		this.storeLabel = storeLabel;
//		setStackTrace(new StackTraceElement[0]); // this is to reduce the clutter in the logs
	}
	
	public String getUserName() {
		return username;
	}
	
	public String getStoreLabel() {
		return storeLabel;
	}
	
}
