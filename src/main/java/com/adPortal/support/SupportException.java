package com.adPortal.support;

public class SupportException extends RuntimeException {
	private final int statusCode;

	public SupportException(int statusCode, String message, Throwable cause) {
		super(message, cause);
		this.statusCode = statusCode;
	}

	public SupportException(int statusCode, String message) {
		super(message);
		this.statusCode = statusCode;
		// TODO Auto-generated constructor stub
	}

	public int getStatusCode() {
		return statusCode;
	}
}
