package com.izettle.assignment.entity;

import static com.izettle.assignment.utils.ArgumentVerifier.verifyNotNull;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import com.datastax.driver.core.utils.UUIDs;

public class IssuedBearerToken {

	private String issuedBearerToken;
	private Timestamp expirationTime;
	private Boolean isValidBearer;
	private String userName;
	private Timestamp createdTimestamp;
	private UUID bearerTs;

	
	public IssuedBearerToken() {
	}

	// Use BearerTokenBuilder
	public IssuedBearerToken(String issuedBearerToken, Timestamp expirationTime, Boolean isValidBearer, String userName,
			Timestamp createdTimestamp) {
		this.issuedBearerToken = issuedBearerToken;
		this.expirationTime = expirationTime;
		this.isValidBearer = isValidBearer;
		this.userName = userName;
		this.createdTimestamp = createdTimestamp;
		this.bearerTs=UUIDs.timeBased();
	}

	public String getIssuedBearerToken() {
		return issuedBearerToken;
	}

	public void setIssuedBearerToken(String issuedBearerToken) {
		this.issuedBearerToken = issuedBearerToken;
	}

	public Timestamp getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(Timestamp expirationTime) {
		this.expirationTime = expirationTime;
	}

	public Boolean getIsValidBearer() {
		return isValidBearer;
	}

	public void setIsValidBearer(Boolean isValidBearer) {
		this.isValidBearer = isValidBearer;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Timestamp getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Timestamp createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public UUID getBearerTs() {
		return bearerTs;
	}

	public void setBearerTs(UUID bearerTs) {
		this.bearerTs = bearerTs;
	}

	public boolean isExpired() {
		return new Date().after(this.expirationTime);
	}

	public void validate() {
		verifyNotNull(issuedBearerToken, "issuedBearerToken");
		verifyNotNull(userName, "userName");
		verifyNotNull(expirationTime, "expirationTime");
		verifyNotNull(isValidBearer, "isValidBearer");
		verifyNotNull(bearerTs, "bearerTS");
		verifyNotNull(createdTimestamp, "createdTimestamp");

	}

}
