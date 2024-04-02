package com.adPortal.history;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

@Entity
public class History {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String adName;
	private long totalToken;
	private String transactionId;
	private String status;
	private long userId;
	private long createdOn;
	private long infoId;
	private long amount;
	private String adsDuration;
	private String feesPaid;

	public History() {
		super();
	}

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

	public long getTotalToken() {
		return totalToken;
	}

	public void setTotalToken(long totalToken) {
		this.totalToken = totalToken;
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

	public long getInfoId() {
		return infoId;
	}

	public void setInfoId(long infoId) {
		this.infoId = infoId;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
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
	
	@PrePersist
	protected void prePersistFunction() {
		this.createdOn = System.currentTimeMillis();
	}

	public History(long id, String adName, long totalToken, String transactionId, String status, long userId,
			long createdOn, long infoId, long amount, String adsDuration, String feesPaid) {
		super();
		this.id = id;
		this.adName = adName;
		this.totalToken = totalToken;
		this.transactionId = transactionId;
		this.status = status;
		this.userId = userId;
		this.createdOn = createdOn;
		this.infoId = infoId;
		this.amount = amount;
		this.adsDuration = adsDuration;
		this.feesPaid = feesPaid;
	}
	
	/////For advertiser 
	public History( String adName, String transactionId, String status, long userId,
			 long infoId,  String adsDuration, String feesPaid) {
		super();
		this.adName = adName;
		this.transactionId = transactionId;
		this.status = status;
		this.userId = userId;
		this.infoId = infoId;
		this.adsDuration = adsDuration;
		this.feesPaid = feesPaid;
	}
	
    /////For viewer
	public History(long amount, String transactionId, String status, long userId, long totalClaim) {
		super();
		this.amount = amount;
		this.transactionId = transactionId;
		this.status = status;
		this.userId = userId;
		this.amount = amount;	}

	@Override
	public String toString() {
		return "History [id=" + id + ", adName=" + adName + ", totalToken=" + totalToken + ", transactionId="
				+ transactionId + ", status=" + status + ", userId=" + userId + ", createdOn=" + createdOn + ", infoId="
				+ infoId + ", amount=" + amount + ", adsDuration=" + adsDuration + ", feesPaid=" + feesPaid + "]";
	}
}