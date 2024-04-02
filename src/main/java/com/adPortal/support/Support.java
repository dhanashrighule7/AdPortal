package com.adPortal.support;

import com.adPortal.ads.Status;
import com.adPortal.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;

@Entity
public class Support {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String issueType;
	private String description;
	private String action;
	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 15)
	private Status status = Status.pending;
	private long createdOn;
	@ManyToOne
	@JoinColumn(name = "userId", nullable = false)
	private User user;

	public Support() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getIssueType() {
		return issueType;
	}

	public void setIssueType(String issueType) {
		this.issueType = issueType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public void markApproved() {
		this.status = Status.solved;
	}

	public void markRejected() {
		this.status = Status.rejected;
	}

	public void markDeleted() {
		this.status = Status.deleted;
	}

	public long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}

	@PrePersist
	protected void prePersistFunction() {
		this.createdOn = System.currentTimeMillis();
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Support(long id, String issueType, String description, String action, Status status, long createdOn,
			User user) {
		this.id = id;
		this.issueType = issueType;
		this.description = description;
		this.action = action;
		this.status = status;
		this.createdOn = createdOn;
		this.user = user;
	}

	@Override
	public String toString() {
		return "Support [id=" + id + ", issueType=" + issueType + ", description=" + description + ", action=" + action
				+ ", status=" + status + ", createdOn=" + createdOn + ", user=" + user + "]";
	}
}
