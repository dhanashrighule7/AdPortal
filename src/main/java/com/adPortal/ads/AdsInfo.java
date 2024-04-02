package com.adPortal.ads;

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
import jakarta.persistence.PreUpdate;

@Entity
public class AdsInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String category;
	private String subCategory;
	private String language;
	private String adsDuration;
	private String adPosition;
	private long createdOn;
	private long updatedOn;
	private long startDate;
	private long endDate;
	private long feesPaid;
	private String totalFees;
	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 15)
	private Status status = Status.pending;
	private String longitude;
	private String lattitude;
	private String fileName;
	private long views;
	private String link;
	private String adTitle;
	private String description;

	private String address;
	private String state;
	private String city;

	@ManyToOne
	@JoinColumn(name = "userId", nullable = false)
	private User user;

	public String getAdTitle() {
		return adTitle;
	}

	public void setAdTitle(String adTitle) {
		this.adTitle = adTitle;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public AdsInfo() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getAdsDuration() {
		return adsDuration;
	}

	public void setAdsDuration(String adsDuration) {
		this.adsDuration = adsDuration;
	}

	public String getAdPosition() {
		return adPosition;
	}

	public void setAdPosition(String adPosition) {
		this.adPosition = adPosition;
	}

	public long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}

	public long getStartDate() {
		return startDate;
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	public long getEndDate() {
		return endDate;
	}

	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}

	public long getFeesPaid() {
		return feesPaid;
	}

	public void setFeesPaid(long feesPaid) {
		this.feesPaid = feesPaid;
	}

	public String getTotalFees() {
		return totalFees;
	}

	public void setTotalFees(String totalFees) {
		this.totalFees = totalFees;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLattitude() {
		return lattitude;
	}

	public void setLattitude(String lattitude) {
		this.lattitude = lattitude;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getViews() {
		return views;
	}

	public void setViews(long views) {
		this.views = views;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Override
	public String toString() {
		return "AdsInfo [id=" + id + ", category=" + category + ", subCategory=" + subCategory + ", language="
				+ language + ", adsDuration=" + adsDuration + ", adPosition=" + adPosition + ", createdOn=" + createdOn
				+ ", updatedOn=" + updatedOn + ", startDate=" + startDate + ", endDate=" + endDate + ", feesPaid="
				+ feesPaid + ", totalFees=" + totalFees + ", status=" + status + ", longitude=" + longitude
				+ ", lattitude=" + lattitude + ", fileName=" + fileName + ", views=" + views + ", link=" + link
				+ ", adTitle=" + adTitle + ", description=" + description + ", address=" + address + ", state=" + state
				+ ", city=" + city + ", user=" + user + ", token=" + token + "]";
	}

	public AdsInfo(long id, String category, String subCategory, String language, String adsDuration, String adPosition,
			long createdOn, long updatedOn, long startDate, long endDate, long feesPaid, String totalFees,
			Status status, String longitude, String lattitude, String fileName, long views, String link, String adTitle,
			String description, String address, String state, String city, User user, Long token) {
		super();
		this.id = id;
		this.category = category;
		this.subCategory = subCategory;
		this.language = language;
		this.adsDuration = adsDuration;
		this.adPosition = adPosition;
		this.createdOn = createdOn;
		this.updatedOn = updatedOn;
		this.startDate = startDate;
		this.endDate = endDate;
		this.feesPaid = feesPaid;
		this.totalFees = totalFees;
		this.status = status;
		this.longitude = longitude;
		this.lattitude = lattitude;
		this.fileName = fileName;
		this.views = views;
		this.link = link;
		this.adTitle = adTitle;
		this.description = description;
		this.address = address;
		this.state = state;
		this.city = city;
		this.user = user;
		this.token = token;
	}

	public long getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(long updatedOn) {
		this.updatedOn = updatedOn;
	}

	public void markApproved() {
		this.status = Status.approved;
	}

	public void markRejected() {
		this.status = Status.rejected;
	}

	@PrePersist
	protected void prePersistFunction() {
		this.createdOn = System.currentTimeMillis();
		this.updatedOn = System.currentTimeMillis();
	}

	@PreUpdate
	protected void preUpdateFunction() {
		this.updatedOn = System.currentTimeMillis();
	}

	private Long token;

	public Long getToken() {
		return token;
	}

	public void setToken(long token) {
		this.token = token;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setToken(Long token) {
		this.token = token;
	}

}
