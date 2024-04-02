package com.adPortal.ads;

public class AdsInfoException extends RuntimeException {
	
	
	private final int statusCode;

    public AdsInfoException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public AdsInfoException(int statusCode, String message) {
    	   super(message);
		this.statusCode =statusCode;
		// TODO Auto-generated constructor stub
	}

	public int getStatusCode() {
        return statusCode;
    }

}
