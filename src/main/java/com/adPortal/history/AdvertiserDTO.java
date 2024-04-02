package com.adPortal.history;


public class AdvertiserDTO {

	private long id;
	private String adName;
	private String adsDuration;
	private String feesPaid;
	private long createdOn;
	private String transactionId;
	private String status;
	private long userId;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getAdName() {
		return adName;
	}
	public void setAdName(String adName) {
		this.adName = adName;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public long getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}
	public String getAdsDuration() {
		return adsDuration;
	}
	public void setAdsDuration(String adsDuration) {
		this.adsDuration = adsDuration;
	}
	public String getFeesPaid() {
		return feesPaid;
	}
	public void setFeesPaid(String feesPaid) {
		this.feesPaid = feesPaid;
	}
	
	public AdvertiserDTO(){}
	
	public AdvertiserDTO(long id, String adName, String transactionId, String status, long userId, long createdOn,
			String adsDuration, String feesPaid) {
		super();
		this.id = id;
		this.adName = adName;
		this.transactionId = transactionId;
		this.status = status;
		this.userId = userId;
		this.createdOn = createdOn;
		this.adsDuration = adsDuration;
		this.feesPaid = feesPaid;
	}
	
	@Override
	public String toString() {
		return "AdvertiserDTO [id=" + id + ", adName=" + adName + ", transactionId=" + transactionId + ", status="
				+ status + ", userId=" + userId + ", createdOn=" + createdOn + ", adsDuration=" + adsDuration
				+ ", feesPaid=" + feesPaid + "]";
	}
	
		
}
