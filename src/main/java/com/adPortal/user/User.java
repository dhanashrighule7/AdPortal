package com.adPortal.user;

import java.util.Arrays;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;

@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(unique = true)
	private String email;
	private String password;
	private String username;
	private String role;
	private String action;
	private long createdOn;
	private long totalCredit;
	private String claimToken;
	private String totalEarnedToken;
	private String claimFrequency;
	private String typesOfAds;
	private long totalAdRan;
	private String walletAddress;

	private String otp;

	@Lob
	@Column(name = "profile", columnDefinition = "LONGBLOB")
	byte[] profile;

	public User() {
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public byte[] getProfile() {
		return profile;
	}

	public void setProfile(byte[] profile) {
		this.profile = profile;
	}

	public String getWalletAddress() {
		return walletAddress;
	}

	public void setWalletAddress(String walletAddress) {
		this.walletAddress = walletAddress;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}

	public long getTotalCredit() {
		return totalCredit;
	}

	public void setTotalCredit(long totalCredit) {
		this.totalCredit = totalCredit;
	}

	public String getTypesOfAds() {
		return typesOfAds;
	}

	public void setTypesOfAds(String typesOfAds) {
		this.typesOfAds = typesOfAds;
	}

	public long getTotalAdRan() {
		return totalAdRan;
	}

	@PrePersist
	protected void prePersistFunction() {
		this.createdOn = System.currentTimeMillis();
	}

	public void setTotalAdRan(long totalAdRan) {
		this.totalAdRan = totalAdRan;
	}

	public String getClaimToken() {
		return claimToken;
	}

	public void setClaimToken(String claimToken) {
		this.claimToken = claimToken;
	}

	public String getTotalEarnedToken() {
		return totalEarnedToken;
	}

	public void setTotalEarnedToken(String totalEarnedToken) {
		this.totalEarnedToken = totalEarnedToken;
	}

	public String getClaimFrequency() {
		return claimFrequency;
	}

	public void setClaimFrequency(String claimFrequency) {
		this.claimFrequency = claimFrequency;
	}

	public User(long id, String email, String password, String username, String role, String action, long createdOn,
			long totalCredit, String claimToken, String totalEarnedToken, String claimFrequency, String typesOfAds,
			long totalAdRan, String walletAddress, String otp, byte[] profile) {
		super();
		this.id = id;
		this.email = email;
		this.password = password;
		this.username = username;
		this.role = role;
		this.action = action;
		this.createdOn = createdOn;
		this.totalCredit = totalCredit;
		this.claimToken = claimToken;
		this.totalEarnedToken = totalEarnedToken;
		this.claimFrequency = claimFrequency;
		this.typesOfAds = typesOfAds;
		this.totalAdRan = totalAdRan;
		this.walletAddress = walletAddress;
		this.otp = otp;
		this.profile = profile;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", email=" + email + ", password=" + password + ", username=" + username + ", role="
				+ role + ", action=" + action + ", createdOn=" + createdOn + ", totalCredit=" + totalCredit
				+ ", claimToken=" + claimToken + ", totalEarnedToken=" + totalEarnedToken + ", claimFrequency="
				+ claimFrequency + ",  typesOfAds=" + typesOfAds + ", totalAdRan=" + totalAdRan + ", walletAddress="
				+ walletAddress + ", otp=" + otp + ", profile=" + Arrays.toString(profile) + "]";
	}
}