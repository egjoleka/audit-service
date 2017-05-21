package com.izettle.assignment.ddo;

import com.izettle.assignment.utils.ArgumentVerifier;

public class UserDisplayEntity {
	private String userName;
	private String password;
	private String confirmedPassword;
	private String firstName;
	private String lastName;

	public UserDisplayEntity(final String userName, final String password, final String firstName,
			final String lastName, final String confirmedPassword) {
		ArgumentVerifier.verifyNotNull(userName, "userName");
		ArgumentVerifier.verifyNotNull(password, "password");
		ArgumentVerifier.verifyNotNull(firstName, "firstName");
		ArgumentVerifier.verifyNotNull(lastName, "lastName");
		ArgumentVerifier.verifyNotNull(confirmedPassword, "confirmedPassword");
		this.userName = userName;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.confirmedPassword = confirmedPassword;
	}

	public UserDisplayEntity() {

	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getConfirmedPassword() {
		return confirmedPassword;
	}

	public void setConfirmedPassword(String confirmedPassword) {
		this.confirmedPassword = confirmedPassword;
	}

}
