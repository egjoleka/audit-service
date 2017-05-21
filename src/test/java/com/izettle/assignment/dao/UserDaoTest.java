package com.izettle.assignment.dao;
// package com.drwp.securityservice.authentication.dao;
//
// import static org.junit.Assert.assertEquals;
// import static org.junit.Assert.assertFalse;
// import static org.junit.Assert.assertNotNull;
// import static org.junit.Assert.assertTrue;
//
// import org.junit.Before;
// import org.junit.Test;
//
// import com.drwp.securityservice.authentication.BaseTest;
// import com.drwp.securityservice.authentication.entity.User;
// import com.drwp.securityservice.authentication.exception.AuthenticationException;
//
// public class UserDaoTest extends BaseTest {
//
// private static final String USERNAME = "NetGiroAdmin";
// private static final String USERNAME2 = "NetGiroAdmin2";
// private final static String PASSWORD = "passdfad";
// private User user1;
// private User user2;
//
// @Before
// public void warmUp() {
// user1 = new User();
// user1.setCreatedBy(CREATED_BY);
// user1.setCreatedTimestamp(createdTimestamp);
// user1.setIsActiveUser(Boolean.TRUE);
// user1.setIsEncryptionEnabled(Boolean.TRUE);
// user1.setIsTwoLeggedUser(Boolean.FALSE);
// user1.setMasterMid(MASTER_MID);
// user1.setPassword(PASSWORD);
// user1.setSalt(SALT);
// user1.setTwoLeggedBearerAttemps(10);
// user1.setTwoLeggedBearerTokenValiditySeconds(3600L);
// user1.setUpdatedBy(UPDATED_BY);
// user1.setUpdatedTimestamp(updatedTimestamp);
// user1.setUserName(USERNAME);
// user1.setFirstName(FIRST_NAME);
// user1.setLastName(LAST_NAME);
// user1.setMobileNumber(MOBILE_NUMBER);
// user1.setFailedLoginAttempts(FAILED_LOGIN_ATTEMP);
// user1.setLastLoginAttempt(createdTimestamp);
// user1.setPasswordExpirationTimestamp(createdTimestamp);
// user1.setIsLockedOut(Boolean.FALSE);
//
// user2 = new User();
// user2.setCreatedBy(CREATED_BY);
// user2.setCreatedTimestamp(createdTimestamp);
// user2.setIsActiveUser(Boolean.TRUE);
// user2.setIsEncryptionEnabled(Boolean.TRUE);
// user2.setIsTwoLeggedUser(Boolean.FALSE);
// user2.setMasterMid(MASTER_MID);
// user2.setPassword(PASSWORD);
// user2.setSalt(SALT);
// user2.setTwoLeggedBearerAttemps(10);
// user2.setTwoLeggedBearerTokenValiditySeconds(3600L);
// user2.setUpdatedBy(UPDATED_BY);
// user2.setUpdatedTimestamp(updatedTimestamp);
// user2.setUserName(USERNAME2);
// user2.setFirstName(FIRST_NAME);
// user2.setLastName(LAST_NAME);
// user2.setMobileNumber(MOBILE_NUMBER);
// user2.setFailedLoginAttempts(FAILED_LOGIN_ATTEMP);
// user2.setLastLoginAttempt(createdTimestamp);
// user2.setPasswordExpirationTimestamp(createdTimestamp);
// user2.setIsLockedOut(Boolean.FALSE);
//
// junitDao.saveUser(user1);
// junitDao.saveUser(user2);
// }
//
// @Test
// public void getUserByUsernameSuccess() {
// final User user = userDao.getUserByUsername(USERNAME);
// assertNotNull(user);
// assertEquals(CREATED_BY, user.getCreatedBy());
// assertNotNull(user.getCreatedTimestamp());
// assertTrue(user.getIsActiveUser());
// assertTrue(user.getIsEncryptionEnabled());
// assertFalse(user.getIsTwoLeggedUser());
// assertEquals(MASTER_MID, user.getMasterMid());
// assertEquals(PASSWORD, user.getPassword());
// assertEquals(SALT, user.getSalt());
// assertEquals(new Integer(10), user.getTwoLeggedBearerAttemps());
// assertEquals(new Long(3600), user.getTwoLeggedBearerTokenValiditySeconds());
// assertEquals(UPDATED_BY, user.getUpdatedBy());
// assertNotNull(user.getUpdatedTimestamp());
// assertEquals(USERNAME, user.getUserName());
// assertEquals(FIRST_NAME, user.getFirstName());
// assertEquals(LAST_NAME, user.getLastName());
// assertEquals(MOBILE_NUMBER, user.getMobileNumber());
// assertEquals(FAILED_LOGIN_ATTEMP, user.getFailedLoginAttempts());
// assertEquals(createdTimestamp, user.getLastLoginAttempt());
// assertEquals(createdTimestamp, user.getPasswordExpirationTimestamp());
// assertEquals(Boolean.FALSE, user.getIsLockedOut());
//
// }
//
// @Test
// public void updateUser() {
// user1.setMobileNumber("0733312529898");
// userDao.updateUser(user1);
// final User user = userDao.getUserByUsername(user1.getUserName());
// assertNotNull(user);
// assertEquals(CREATED_BY, user.getCreatedBy());
// assertNotNull(user.getCreatedTimestamp());
// assertTrue(user.getIsActiveUser());
// assertTrue(user.getIsEncryptionEnabled());
// assertFalse(user.getIsTwoLeggedUser());
// assertEquals(MASTER_MID, user.getMasterMid());
// assertEquals(PASSWORD, user.getPassword());
// assertEquals(SALT, user.getSalt());
// assertEquals(new Integer(10), user.getTwoLeggedBearerAttemps());
// assertEquals(new Long(3600), user.getTwoLeggedBearerTokenValiditySeconds());
// assertEquals(UPDATED_BY, user.getUpdatedBy());
// assertNotNull(user.getUpdatedTimestamp());
// assertEquals(USERNAME, user.getUserName());
// assertEquals(FIRST_NAME, user.getFirstName());
// assertEquals(LAST_NAME, user.getLastName());
// assertEquals("0733312529898", user.getMobileNumber());
// assertEquals(FAILED_LOGIN_ATTEMP, user.getFailedLoginAttempts());
// assertEquals(createdTimestamp, user.getLastLoginAttempt());
// assertEquals(createdTimestamp, user.getPasswordExpirationTimestamp());
// assertEquals(Boolean.FALSE, user.getIsLockedOut());
//
// }
//
// @Test(expected = AuthenticationException.class)
// public void getUserByUsernameNotFound() {
// final User user = userDao.getUserByUsername("USERNAME");
// assertNotNull(user);
// assertEquals(CREATED_BY, user.getCreatedBy());
// assertNotNull(user.getCreatedTimestamp());
// assertTrue(user.getIsActiveUser());
// assertTrue(user.getIsEncryptionEnabled());
// assertFalse(user.getIsTwoLeggedUser());
// assertEquals(MASTER_MID, user.getMasterMid());
// assertEquals(PASSWORD, user.getPassword());
// assertEquals(SALT, user.getSalt());
// assertEquals(new Integer(10), user.getTwoLeggedBearerAttemps());
// assertEquals(new Long(3600), user.getTwoLeggedBearerTokenValiditySeconds());
// assertEquals(UPDATED_BY, user.getUpdatedBy());
// assertNotNull(user.getUpdatedTimestamp());
// assertEquals(USERNAME, user.getUserName());
//
// }
//
// }
