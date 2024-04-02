package com.adPortal.Exception;

import java.util.Date;

public class Error {
	
	
	private int errorCode;
	private String developermsg;
	private String msg;
	private long date;
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getDevelopermsg() {
		return developermsg;
	}
	public void setDevelopermsg(String developermsg) {
		this.developermsg = developermsg;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}
	public Error(int errorCode, String developermsg, String msg, long l) {
		super();
		this.errorCode = errorCode;
		this.developermsg = developermsg;
		this.msg = msg;
		this.date = l;
	}
	
	public Error(int errorCode, String developermsg, String msg) {
		super();
		this.errorCode = errorCode;
		this.developermsg = developermsg;
		this.msg = msg;
		
	}
	@Override
	public String toString() {
		return "Error [errorCode=" + errorCode + ", developermsg=" + developermsg + ", msg=" + msg + ", date=" + date
				+ "]";
	}
	public Error() {
		super();
	
	}
	
	

}
