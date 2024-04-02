package com.adPortal.user;

import com.adPortal.Exception.GlobalException;

public class UserControllerException extends RuntimeException {

	/**
	 * 
	 */
//	private static final long serialVersionUID = 1L;
//
//	public UserControllerException(int errorcode, String developermsg, String msg) {
//		
//		super(errorcode, developermsg, msg);
//		System.out.println("Inside the exception class");
//	}
//
//	public UserControllerException(int errorcode, String developermsg, String msg, Throwable cause) {
//		super(errorcode, developermsg, msg, cause);
//	}
	
	
	 private final int statusCode;

	    public UserControllerException(int statusCode, String message, Throwable cause) {
	        super(message, cause);
	        this.statusCode = statusCode;
	    }

	    public UserControllerException(int statusCode, String message) {
	    	   super(message);
			this.statusCode =statusCode;
			// TODO Auto-generated constructor stub
		}

		public int getStatusCode() {
	        return statusCode;
	    }

}
