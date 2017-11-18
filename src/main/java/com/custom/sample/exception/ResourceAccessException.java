package com.custom.sample.exception;

public class ResourceAccessException extends RuntimeException {

	private static final long serialVersionUID = 8920779768938223794L;

	public ResourceAccessException(String message) {
		super(message);
	}

	public ResourceAccessException(String message, Throwable e) {
		super(message, e);
	}
}
