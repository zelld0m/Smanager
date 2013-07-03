package com.search.manager.response;

import java.io.Serializable;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.convert.BeanConverter;

@DataTransferObject(converter = BeanConverter.class)
public class ErrorMessage<T> implements Serializable {

	private static final long serialVersionUID = -2276136084548650967L;

	// Error message
	private String message;

	// Error data to be used by UI when displaying custom error messages.
	private T data;

	public ErrorMessage() {
	}

	public ErrorMessage(String message) {
		this.message = message;
	}

	public ErrorMessage(String message, T data) {
		this.message = message;
		this.data = data;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
