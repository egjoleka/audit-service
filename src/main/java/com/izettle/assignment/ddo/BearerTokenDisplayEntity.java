package com.izettle.assignment.ddo;

public class BearerTokenDisplayEntity {

	private String bearerToken;
	private String expirationTimestamp;

	public BearerTokenDisplayEntity() {
		//cxf needs it
	}

	public String getBearerToken() {
		return bearerToken;
	}

	public void setBearerToken(String bearerToken) {
		this.bearerToken = bearerToken;
	}

	public String getExpirationTimestamp() {
		return expirationTimestamp;
	}

	public void setExpirationTimestamp(String expirationTimestamp) {
		this.expirationTimestamp = expirationTimestamp;
	}

	public BearerTokenDisplayEntity(String bearerToken, String expirationTimestamp) {
		this.bearerToken = bearerToken;
		this.expirationTimestamp = expirationTimestamp;
	}

}
