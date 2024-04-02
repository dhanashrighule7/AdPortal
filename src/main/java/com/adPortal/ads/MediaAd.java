package com.adPortal.ads;

import java.util.Arrays;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
@Entity
public class MediaAd {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private long userId;
	private long adInfoId;
	@Lob
	@Column(name = "media", columnDefinition = "LONGBLOB")
	@Basic(fetch = FetchType.LAZY)
	private byte[] media;
	private String extension;
	private String category;
	private long createdOn;
	private String fileName;
	private String link;
	private String subCategory;
	private String accessLink;
	
	public MediaAd() {}
	
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
	public long getAdInfoId() {
		return adInfoId;
	}
	public void setAdInfoId(long adInfoId) {
		this.adInfoId = adInfoId;
	}
	public byte[] getMedia() {
		return media;
	}
	public void setMedia(byte[] media) {
		this.media = media;
	}
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public long getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
	@PrePersist
	protected void prePersistFunction() {
		this.createdOn = System.currentTimeMillis();
	}

	@Override
	public String toString() {
		return "MediaAd [id=" + id + ", userId=" + userId + ", adInfoId=" + adInfoId + ", media="
				+ Arrays.toString(media) + ", extension=" + extension + ", category=" + category + ", createdOn="
				+ createdOn + ", fileName=" + fileName + ", link=" + link + ", subCategory=" + subCategory
				+ ", accessLink=" + accessLink + "]";
	}

	public MediaAd( long userId, long adInfoId,  String category, long createdOn,
			 String link,String subCategory) {
		super();
		this.userId = userId;
		this.adInfoId = adInfoId;
		this.category = category;
		this.createdOn = createdOn;
		this.link = link;
		this.subCategory = subCategory;
	}
	
	public MediaAd( long userId, long adInfoId, byte[] media, String extension, String category, long createdOn,
			String fileName,String subCategory) {
		super();
		this.userId = userId;
		this.adInfoId = adInfoId;
		this.media = media;
		this.extension = extension;
		this.category = category;
		this.createdOn = createdOn;
		this.fileName = fileName;
		this.subCategory = subCategory;
	}

	public MediaAd(long id, long userId, long adInfoId, byte[] media, String extension, String category, long createdOn,
			String fileName,String subCategory, String accessLink) {
		super();
		this.id = id;
		this.userId = userId;
		this.adInfoId = adInfoId;
		this.media = media;
		this.extension = extension;
		this.category = category;
		this.createdOn = createdOn;
		this.fileName = fileName;
		this.accessLink = accessLink;
		this.subCategory = subCategory;
	}

	public String getAccessLink() {
		return accessLink;
	}

	public void setAccessLink(String accessLink) {
		this.accessLink = accessLink;
	}

	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}


}