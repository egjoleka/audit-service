package com.izettle.assignment.ddo;

import java.sql.Timestamp;

public class BearerTokenDisplayEntity {

	private String bearerToken;
	private Timestamp expirationTimestamp;

	public BearerTokenDisplayEntity() {
		//cxf needs it
	}

	public String getBearerToken() {
		return bearerToken;
	}

	public void setBearerToken(String bearerToken) {
		this.bearerToken = bearerToken;
	}

	public Timestamp getExpirationTimestamp() {
		return expirationTimestamp;
	}

	public void setExpirationTimestamp(Timestamp expirationTimestamp) {
		this.expirationTimestamp = expirationTimestamp;
	}

	public BearerTokenDisplayEntity(String bearerToken, Timestamp expirationTimestamp) {
		this.bearerToken = bearerToken;
		this.expirationTimestamp = expirationTimestamp;
	}

}
