package com.adPortal.Notifications;

public class NotificationException extends RuntimeException {

	/**
	 * 
	 */
//	private static final long serialVersionUID = 1L;

	private final int statusCode;

	public NotificationException(int statusCode, String message, Throwable cause) {
		super(message, cause);
		this.statusCode = statusCode;
	}

	public NotificationException(int statusCode, String message) {
		super(message);
		this.statusCode = statusCode;
		// TODO Auto-generated constructor stub
	}

	public int getStatusCode() {
		return statusCode;
	}

}
