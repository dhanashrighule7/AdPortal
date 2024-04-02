package com.adPortal.ads;

import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

@Entity
public class Views {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private long userId;
	private long infoId;
	private LocalDate date;
	private long views;

	public Views() {
	}

	public Views(long id, long userId, long infoId, LocalDate date, long views) {
		super();
		this.id = id;
		this.userId = userId;
		this.infoId = infoId;
		this.date = date;
		this.views = views;
	}

	public Views(long userId, long infoId, long views) {
		super();
		this.userId = userId;
		this.infoId = infoId;
		this.views = views;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getInfoId() {
		return infoId;
	}

	public void setInfoId(long infoId) {
		this.infoId = infoId;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public long getViews() {
		return views;
	}

	public void setViews(long views) {
		this.views = views;
	}

	@PrePersist
	protected void prePersistFunction() {
		this.date = LocalDate.now();
	}

	@Override
	public String toString() {
		return "Views [id=" + id + ", userId=" + userId + ", infoId=" + infoId + ", date=" + date + ", views=" + views
				+ "]";
	};
}