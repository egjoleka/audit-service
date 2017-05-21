package com.izettle.assignment.entity;

import java.sql.Timestamp;

public class User {

    private String userName;
    private String password;
    private String salt;
    private Boolean isActiveUser;
    private Timestamp createdTimestamp;
    private String firstName;
    private String lastName;
    
	public User() {
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
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	public Boolean getIsActiveUser() {
		return isActiveUser;
	}
	public void setIsActiveUser(Boolean isActiveUser) {
		this.isActiveUser = isActiveUser;
	}
	public Timestamp getCreatedTimestamp() {
		return createdTimestamp;
	}
	public void setCreatedTimestamp(Timestamp createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
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

}
