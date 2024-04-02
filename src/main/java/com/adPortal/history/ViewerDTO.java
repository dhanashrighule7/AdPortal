package com.adPortal.history;

public class ViewerDTO {

	private long id;
	private long totalToken;
	private long amount;
	private long createdOn;
	private String transactionId;
	private String status;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
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
	public long getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	
	public ViewerDTO() {}
	
	public ViewerDTO(long id, long totalToken, String transactionId, String status, long createdOn,
			long amount) {
		super();
		this.id = id;
		this.totalToken = totalToken;
		this.transactionId = transactionId;
		this.status = status;
		this.createdOn = createdOn;
		this.amount = amount;
	}
	
	@Override
	public String toString() {
		return "ViewerDTO [id=" + id + ", totalToken=" + totalToken + ", transactionId=" + transactionId + ", status="
				+ status + ",  createdOn=" + createdOn + ", amount=" + amount + "]";
	}
	
		
}
