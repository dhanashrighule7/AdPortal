package com.adPortal.payments;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

@Entity
public class PaymentsData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String transactionId;
	private String receiver;
	private String sender;
	private String status;
	private String value;
	private long createdOn;
	private long userId;
	private long infoId;
	private long totalToken;
	
	
	public PaymentsData() {
		super();
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	@PrePersist
	protected void prePersistFunction() {
		this.createdOn = System.currentTimeMillis();
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

	public long getTotalToken() {
		return totalToken;
	}

	public void setTotalToken(long totalToken) {
		this.totalToken = totalToken;
	}

	public PaymentsData(long id, String transactionId, String receiver, String sender, String status, String value,
			long createdOn, long userId, long infoId, long totalToken) {
		super();
		this.id = id;
		this.transactionId = transactionId;
		this.receiver = receiver;
		this.sender = sender;
		this.status = status;
		this.value = value;
		this.createdOn = createdOn;
		this.userId = userId;
		this.infoId = infoId;
		this.totalToken = totalToken;
	}

	public PaymentsData(String string, String string2, String string3, String string4, String feesPaid, long id2,
			long id3) {
	}

	@Override
	public String toString() {
		return "PaymentsData [id=" + id + ", transactionId=" + transactionId + ", receiver=" + receiver + ", sender="
				+ sender + ", status=" + status + ", value=" + value + ", createdOn=" + createdOn + ", userId=" + userId
				+ ", infoId=" + infoId + ", totalToken=" + totalToken + "]";
	}

}