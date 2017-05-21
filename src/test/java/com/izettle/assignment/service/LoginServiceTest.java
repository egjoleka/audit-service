package com.izettle.assignment.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.Date;
import org.apache.commons.configuration.ConfigurationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.datastax.driver.core.utils.UUIDs;
import com.google.gson.Gson;
import com.izettle.assignment.AppConstants;
import com.izettle.assignment.crypto.PasswordCrypto;
import com.izettle.assignment.dao.IssuedBearerTokenDao;
import com.izettle.assignment.dao.LoginAuditsDao;
import com.izettle.assignment.dao.UserDao;
import com.izettle.assignment.ddo.UserDisplayEntity;
import com.izettle.assignment.entity.IssuedBearerToken;
import com.izettle.assignment.entity.LoginAudit;
import com.izettle.assignment.entity.User;
import com.izettle.assignment.exception.IzettleException;
import com.izettle.assignment.utils.BearerRandomGenerator;
import com.izettle.assignment.utils.IzettleUtils;

@RunWith(MockitoJUnitRunner.class)
public class LoginServiceTest {

	private static Gson gson = new Gson();
	private static final String USERNAME = "gjolekae@gmail.com";
	private static final String ENCRYPTED_PASSWORD = "AXYkW0o+dm7oBF1CeJ0oJPOCse0=";
	private static final String PASSWORD = "Informatika13?";
	private static final String WRONG_PASS = "pass";
	private static final String SALT = "+KxCzMUCFa4=";
	private static final String FIRST_NAME = "edrin";
	private static final String LAST_NAME = "gjoleka";
	private static final String BEARER = "111111111111111111111111";
	private final LoginAuditsDao loginAuditsDao = Mockito.mock(LoginAuditsDao.class);
	private final IssuedBearerTokenDao issuedBearerTokenDao = Mockito.mock(IssuedBearerTokenDao.class);
	private final UserDao userDao = Mockito.mock(UserDao.class);
	private final BearerRandomGenerator bearerRandomGenerator = Mockito.mock(BearerRandomGenerator.class);
	private final PasswordCrypto passwordCrypto = Mockito.mock(PasswordCrypto.class);
	private LoginService loginService;
	final User user = new User();
	final UserDisplayEntity userDisplayEntity = new UserDisplayEntity();
	final IssuedBearerToken issuedBearerToken = new IssuedBearerToken();
	private LoginAudit loginAudit ;

	@Before
	public void setUp() throws ConfigurationException {
		createTestUser();
		createTestUserDisplay();
		createTestBearerToken();
		loginAudit = LoginAudit.createInstance(USERNAME, AppConstants.OK, AppConstants.OK, BEARER,
				Boolean.TRUE);
		loginService = new LoginService(issuedBearerTokenDao, loginAuditsDao, userDao, bearerRandomGenerator,
				passwordCrypto);
		when(userDao.getUserByUsername(USERNAME)).thenReturn(user);
		when(passwordCrypto.generateSalt()).thenReturn(IzettleUtils.decodeFromBase64Bytes(SALT));

		Mockito.doNothing().when(userDao).storeUser(user);
		Mockito.doNothing().when(issuedBearerTokenDao).createBearerTokens(issuedBearerToken);
		Mockito.doNothing().when(loginAuditsDao).store(loginAudit);

	}

	protected void createTestBearerToken() {
		issuedBearerToken.setBearerTs(UUIDs.timeBased());
		issuedBearerToken.setCreatedTimestamp(getNowTimestamp());
		issuedBearerToken.setExpirationTime(getExpirationTimestamp());
		issuedBearerToken.setIssuedBearerToken(BEARER);
		issuedBearerToken.setIsValidBearer(Boolean.FALSE);
		issuedBearerToken.setUserName(USERNAME);
	}

	protected void createTestUserDisplay() {
		userDisplayEntity.setFirstName(FIRST_NAME);
		userDisplayEntity.setLastName(LAST_NAME);
		userDisplayEntity.setUserName(USERNAME);
		userDisplayEntity.setPassword(PASSWORD);
		userDisplayEntity.setConfirmedPassword(PASSWORD);
	}

	protected void createTestUser() {
		user.setCreatedTimestamp(getNowTimestamp());
		user.setFirstName(FIRST_NAME);
		user.setLastName(LAST_NAME);
		user.setIsActiveUser(Boolean.TRUE);
		user.setPassword(ENCRYPTED_PASSWORD);
		user.setSalt(SALT);
		user.setUserName(USERNAME);
	}

	@Test
	public void registerUserSuceessTest() {
		when(userDao.checkIfUserExists(USERNAME)).thenReturn(Boolean.FALSE);
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

		loginService.registerUser(userDisplayEntity);

		Mockito.verify(userDao, Mockito.times(1)).storeUser(userCaptor.capture());
		assertTrue(userCaptor.getValue().getIsActiveUser());
		assertEquals(USERNAME, userCaptor.getValue().getUserName());
		assertEquals(FIRST_NAME, userCaptor.getValue().getFirstName());
		assertEquals(LAST_NAME, userCaptor.getValue().getLastName());
		assertEquals(ENCRYPTED_PASSWORD, userCaptor.getValue().getPassword());
		assertEquals(SALT, userCaptor.getValue().getSalt());
		assertNotNull(userCaptor.getValue().getCreatedTimestamp());

	}

	@Test(expected = IzettleException.class)
	public void registerUserAlreadyExistingFailureTest() {
		when(userDao.checkIfUserExists(USERNAME)).thenReturn(Boolean.TRUE);
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

		loginService.registerUser(userDisplayEntity);

		Mockito.verify(userDao, Mockito.times(0)).storeUser(userCaptor.capture());

	}
	
	@Test(expected = IzettleException.class)
	public void verifyFailureTestNotMachingPassword() {
		when(userDao.checkIfUserExists(USERNAME)).thenReturn(Boolean.FALSE);
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		ArgumentCaptor<LoginAudit> loginAuditCapture = ArgumentCaptor.forClass(LoginAudit.class);

		loginService.verifyLoginIn(USERNAME, "adfafafafaf");

		
		

	}
	
	@Test
	public void verifySuccess() {
		when(userDao.checkIfUserExists(USERNAME)).thenReturn(Boolean.FALSE);
		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		ArgumentCaptor<LoginAudit> loginAuditCapture = ArgumentCaptor.forClass(LoginAudit.class);
		ArgumentCaptor<IssuedBearerToken> tokenCapture = ArgumentCaptor.forClass(IssuedBearerToken.class);

		loginService.verifyLoginIn(USERNAME, PASSWORD);

		Mockito.verify(issuedBearerTokenDao, Mockito.times(1)).createBearerTokens(tokenCapture.capture());
		Mockito.verify(loginAuditsDao, Mockito.times(1)).store(loginAuditCapture.capture());
		assertTrue(userCaptor.getValue().getIsActiveUser());
		assertEquals(USERNAME, loginAuditCapture.getValue().getUserName());
		assertEquals(BEARER, loginAuditCapture.getValue().getBearer());
		assertTrue(!loginAuditCapture.getValue().getIsSucess());
		assertNotNull(loginAuditCapture.getValue().getRequestTimestamp());

	}

	private Timestamp getNowTimestamp() {
		return new Timestamp(new Date().getTime());
	}

	private Timestamp getExpirationTimestamp() {
		return new Timestamp(new Date().getTime() + AppConstants.TOKEN_EXP_MS);
	}

	//
	// @Test(expected = AuthenticationException.class)
	// public void unlockAccountTestCodeAlreadyUsed() {
	// Mockito.when(dataAccessService.getValidAuthorizationCodeByAuthCode(CODE))
	// .thenThrow(new
	// AuthenticationException(Response.status(Status.BAD_REQUEST).build()));
	// user.setIsLockedOut(Boolean.TRUE);
	// loginService.unlockAccount(CODE);
	// }
	//
	// @Test
	// public void unlockAccountTestAlreadyUnlocked() {
	// user.setIsLockedOut(Boolean.FALSE);
	// authorizationCode.setAuthzCodeType(AuthorizationCodeType.LOCK_OUT);
	// loginService.unlockAccount(CODE);
	// assertTrue(!user.getIsLockedOut());
	// }
	//
	// @Test(expected = AuthenticationException.class)
	// public void unlockAccountTestExpiredCodeFailure() {
	// Mockito.when(dataAccessService.getValidAuthorizationCodeByAuthCode(CODE))
	// .thenThrow(new
	// AuthenticationException(Response.status(Status.BAD_REQUEST).build()));
	// user.setIsLockedOut(Boolean.TRUE);
	// authorizationCode.setCodeExpirationTime(new Timestamp(new
	// DateTime().getMillis()));
	// loginService.unlockAccount(CODE);
	// }
	//
	// @Test
	// public void loginUserTestSuccess() throws URISyntaxException {
	// ArgumentCaptor<String> loginAudit =
	// ArgumentCaptor.forClass(String.class);
	// assertEquals(null, user.getPasswordExpirationTimestamp());
	// final LoginResponse loginResponse = loginService.loginUser(USERNAME,
	// PASSWORD, CLIENT_APPLICATION_ID, REQUEST_URL);
	// assertTrue(loginResponse.isLastLoginLeg());
	// assertEquals("http://pcc.drwp.com:8080/?code=test1234567&tx=10101000",
	// loginResponse.getUri().toString());
	// ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
	// Mockito.verify(dataAccessService,
	// Mockito.times(2)).updateUser(userCaptor.capture());
	// assertTrue(userCaptor.getValue().getIsActiveUser());
	// assertNotNull(userCaptor.getValue().getPasswordExpirationTimestamp());
	// assertNotNull(userCaptor.getValue().getLastLoginAttempt());
	// assertTrue(!userCaptor.getValue().getIsLockedOut());
	// assertEquals(0,
	// userCaptor.getValue().getFailedLoginAttempts().intValue());
	// Mockito.verify(dataAccessService,
	// Mockito.times(1)).storeLoginAudit(Matchers.anyString(),
	// Matchers.anyString(),
	// Matchers.anyString(), loginAudit.capture(),
	// Matchers.any());
	// assertEquals(AppConstants.LOGIN_AUDIT_SUCCESS, loginAudit.getValue());
	// }
	//
	// @Test
	// public void loginUserTwoFactorTestSuccess() throws URISyntaxException {
	// user.setIsTwoFactorAuthenticationEnabled(Boolean.TRUE);
	// user.setTwoFactorAuthenticationType(TwoFactorAuthType.SMS.name());
	// Mockito.doNothing().when(smsService).sendSms(Matchers.anyString(),
	// Matchers.any(SmsCode.class));
	// ArgumentCaptor<String> toEmail = ArgumentCaptor.forClass(String.class);
	// ArgumentCaptor<SmsCode> code = ArgumentCaptor.forClass(SmsCode.class);
	// ArgumentCaptor<String> loginAudit =
	// ArgumentCaptor.forClass(String.class);
	// assertEquals(null, user.getPasswordExpirationTimestamp());
	// final LoginResponse loginResponse = loginService.loginUser(USERNAME,
	// PASSWORD, CLIENT_APPLICATION_ID, REQUEST_URL);
	// final URI uri = loginResponse.getUri();
	// assertFalse(loginResponse.isLastLoginLeg());
	// Mockito.verify(smsService, Mockito.times(1)).sendSms(toEmail.capture(),
	// code.capture());
	// assertEquals("http://authn.drwp.com/#/sms/authentications?authzCode=test1234567&applicationId=pcc&applicationUri=http%3A%2F%2Fpcc.drwp.com%3A8080%2F%3Ftx%3D10101000",
	// uri.toString());
	// ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
	// Mockito.verify(dataAccessService,
	// Mockito.times(1)).updateUser(userCaptor.capture());
	// assertTrue(userCaptor.getValue().getIsActiveUser());
	// assertNotNull(userCaptor.getValue().getPasswordExpirationTimestamp());
	// assertNotNull(userCaptor.getValue().getLastLoginAttempt());
	// assertFalse(userCaptor.getValue().getIsLockedOut());
	// assertEquals(0,
	// userCaptor.getValue().getFailedLoginAttempts().intValue());
	// Mockito.verify(dataAccessService,
	// Mockito.times(0)).storeLoginAudit(Matchers.anyString(),
	// Matchers.anyString(),
	// Matchers.anyString(), loginAudit.capture(),
	// Matchers.any());
	// }
	//
	// @Test(expected = AuthenticationException.class)
	// public void loginUserTestPasswordExpiredFailure() throws
	// URISyntaxException {
	// ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
	// ArgumentCaptor<String> loginAudit =
	// ArgumentCaptor.forClass(String.class);
	// user.setPasswordExpirationTimestamp(new Timestamp(new
	// DateTime().minusHours(2).getMillis()));
	// when(dataAccessService.getUserByUsername(USERNAME)).thenReturn(user);
	// loginService.loginUser(USERNAME, PASSWORD, CLIENT_APPLICATION_ID,
	// REQUEST_URL);
	// Mockito.verify(dataAccessService,
	// Mockito.times(1)).updateUser(userCaptor.capture());
	// Mockito.verify(dataAccessService,
	// Mockito.times(1)).storeLoginAudit(Matchers.anyString(),
	// Matchers.anyString(),
	// Matchers.anyString(), loginAudit.capture(),
	// Matchers.any());
	// assertTrue(!userCaptor.getValue().getIsActiveUser());
	// assertEquals(AppConstants.LOGIN_AUDIT_FAILED, loginAudit.getValue());
	// }
	//
	// @Test(expected = AuthenticationException.class)
	// public void loginUserTestNotActiveUserFailure() throws URISyntaxException
	// {
	// Mockito.when(dataAccessService.getUserByUsername(USERNAME))
	// .thenThrow(new AuthenticationException(Response.serverError().build()));
	// user.setIsActiveUser(Boolean.FALSE);
	// loginService.loginUser(USERNAME, PASSWORD, CLIENT_APPLICATION_ID,
	// CLIENT_APPLICATION_URL);
	// }
	//
	// @Test(expected = AuthenticationException.class)
	// public void loginUserTest_TwoLeggedUser() throws URISyntaxException {
	// user.setIsTwoLeggedUser(true);
	// loginService.loginUser(USERNAME, PASSWORD, CLIENT_APPLICATION_ID,
	// CLIENT_APPLICATION_URL);
	// }
	//
	// @Test(expected = AuthenticationException.class)
	// public void loginUserTestWrongPassFailure() throws URISyntaxException {
	// user.setPassword("sdsafa");
	// loginService.loginUser(USERNAME, PASSWORD, CLIENT_APPLICATION_ID,
	// CLIENT_APPLICATION_URL);
	// }
	//
	// @Test
	// public void loginUserTestWrongPassFailureLockedAccount() throws
	// URISyntaxException, InterruptedException {
	//
	// logInWrongPass("Invalid username or password");
	// assertEquals(Boolean.FALSE, user.getIsLockedOut());
	// assertEquals(new Integer(1), user.getFailedLoginAttempts());
	//
	// logInWrongPass("Invalid username or password");
	// assertEquals(Boolean.FALSE, user.getIsLockedOut());
	// assertEquals(new Integer(2), user.getFailedLoginAttempts());
	//
	// logInWrongPass("Invalid username or password");
	// assertEquals(Boolean.TRUE, user.getIsLockedOut());
	// assertEquals(new Integer(2), user.getFailedLoginAttempts());
	//
	// logInWrongPass("Invalid username or password");
	// assertEquals(Boolean.TRUE, user.getIsLockedOut());
	// assertEquals(new Integer(2), user.getFailedLoginAttempts());
	//
	// Thread.sleep(4000L);
	//
	// loginService.loginUser(USERNAME, PASSWORD, CLIENT_APPLICATION_ID,
	// CLIENT_APPLICATION_URL);
	// final LoginResponse loginResponse = loginService.loginUser(USERNAME,
	// PASSWORD, CLIENT_APPLICATION_ID, REQUEST_URL);
	// assertTrue(loginResponse.isLastLoginLeg());
	// assertEquals(Boolean.FALSE, user.getIsLockedOut());
	// assertEquals(new Integer(0), user.getFailedLoginAttempts());
	// assertEquals("http://pcc.drwp.com:8080/?code=test1234567&tx=10101000",
	// loginResponse.getUri().toString());
	//
	// }
	//
	// private void logInWrongPass(final String message) throws
	// URISyntaxException {
	// try {
	// loginService.loginUser(USERNAME, WRONG_PASS, CLIENT_APPLICATION_ID,
	// CLIENT_APPLICATION_URL);
	// } catch (AuthenticationException x) {
	// final String jsonResponse = x.getResponse().getEntity().toString();
	// checkResponse(jsonResponse, message);
	// }
	// }
	//
	// private void checkResponse(final String jsonResponse, final String
	// message) {
	// ErrorResponse errorResponse = gson.fromJson(jsonResponse,
	// ErrorResponse.class);
	// assertNotNull(errorResponse);
	// assertEquals("invalid_request", errorResponse.getError().name());
	// assertEquals(message, errorResponse.getError_description());
	// }
	//
	// @Test(expected = AuthenticationException.class)
	// public void loginUserTestWrongSaltFailure() throws URISyntaxException {
	// user.setSalt("ddf");
	// loginService.loginUser(USERNAME, PASSWORD, CLIENT_APPLICATION_ID,
	// CLIENT_APPLICATION_URL);
	// }
	//
	// @Test(expected = AuthenticationException.class)
	// public void loginUserTestNoCodeFailure() throws URISyntaxException {
	// when(authorizationCodeGenerator.generateValue()).thenReturn(null);
	// loginService.loginUser(USERNAME, PASSWORD, CLIENT_APPLICATION_ID,
	// CLIENT_APPLICATION_URL);
	// }
	//
	// @Test(expected = AuthenticationException.class)
	// public void loginUserTestEmptyCodeFailure() throws URISyntaxException {
	// when(authorizationCodeGenerator.generateValue()).thenReturn("");
	// loginService.loginUser(USERNAME, PASSWORD, CLIENT_APPLICATION_ID,
	// CLIENT_APPLICATION_URL);
	// }
	//
	// @Test
	// public void loginUserTestSuccess_WithLDAP() throws URISyntaxException {
	// user.setMasterMid(AppConstants.DRWP_MASTER_MID);
	// user.setUserDirectory(UserDirectory.LDAP.name());
	// Mockito.when(ldapAuthnService.authenticateUser(USERNAME,
	// PASSWORD)).thenReturn(true);
	// ArgumentCaptor<String> loginAudit =
	// ArgumentCaptor.forClass(String.class);
	// final LoginResponse loginResponse = loginService.loginUser(USERNAME,
	// PASSWORD, CLIENT_APPLICATION_ID, REQUEST_URL);
	// assertEquals("http://pcc.drwp.com:8080/?code=test1234567&tx=10101000",
	// loginResponse.getUri().toString());
	// assertTrue(loginResponse.isLastLoginLeg());
	// ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
	// Mockito.verify(dataAccessService,
	// Mockito.times(1)).updateUser(userCaptor.capture());
	// Mockito.verify(ldapAuthnService,
	// Mockito.times(1)).authenticateUser(Matchers.anyString(),
	// Matchers.anyString());
	// assertTrue(userCaptor.getValue().getIsActiveUser());
	// assertNull(userCaptor.getValue().getPasswordExpirationTimestamp());
	// assertNotNull(userCaptor.getValue().getLastLoginAttempt());
	// assertTrue(!userCaptor.getValue().getIsLockedOut());
	// assertEquals(0,
	// userCaptor.getValue().getFailedLoginAttempts().intValue());
	// Mockito.verify(dataAccessService,
	// Mockito.times(1)).storeLoginAudit(Matchers.anyString(),
	// Matchers.anyString(),
	// Matchers.anyString(), loginAudit.capture(),
	// Matchers.any());
	// assertEquals(AppConstants.LOGIN_AUDIT_SUCCESS, loginAudit.getValue());
	// Mockito.verify(addressValidator,
	// Mockito.times(1)).isValidIpAddress(Matchers.anyString(),
	// Matchers.anyString());
	// }
	//
	// @Test
	// public void loginUserTestFailure_WithLDAP() throws URISyntaxException {
	// user.setMasterMid(AppConstants.DRWP_MASTER_MID);
	// user.setUserDirectory(UserDirectory.LDAP.name());
	// Mockito.when(ldapAuthnService.authenticateUser(USERNAME,
	// PASSWORD)).thenReturn(false);
	// ArgumentCaptor<String> loginAudit =
	// ArgumentCaptor.forClass(String.class);
	// try {
	// loginService.loginUser(USERNAME, PASSWORD, CLIENT_APPLICATION_ID,
	// REQUEST_URL);
	// Assert.fail("Should have failed");
	// } catch (AuthenticationException x) {
	// final String jsonResponse = x.getResponse().getEntity().toString();
	// checkResponse(jsonResponse, "Invalid username or password");
	// }
	// ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
	// Mockito.verify(dataAccessService,
	// Mockito.times(1)).updateUser(userCaptor.capture());
	// Mockito.verify(ldapAuthnService,
	// Mockito.times(1)).authenticateUser(Matchers.anyString(),
	// Matchers.anyString());
	// assertTrue(userCaptor.getValue().getIsActiveUser());
	// assertNull(userCaptor.getValue().getPasswordExpirationTimestamp());
	// assertNotNull(userCaptor.getValue().getLastLoginAttempt());
	// assertTrue(!userCaptor.getValue().getIsLockedOut());
	// assertEquals(1,
	// userCaptor.getValue().getFailedLoginAttempts().intValue());
	// Mockito.verify(dataAccessService,
	// Mockito.times(1)).storeLoginAudit(Matchers.anyString(),
	// Matchers.anyString(),
	// Matchers.anyString(), loginAudit.capture(),
	// Matchers.any());
	// Mockito.verify(addressValidator,
	// Mockito.times(1)).isValidIpAddress(Matchers.anyString(),
	// Matchers.anyString());
	// assertEquals(AppConstants.LOGIN_AUDIT_FAILED, loginAudit.getValue());
	// }
	//
	// @Test
	// public void loginUserTestFailure_IPNotInRange() throws URISyntaxException
	// {
	// Mockito.reset(addressValidator);
	// Mockito.when(addressValidator.isValidIpAddress(Matchers.anyString(),
	// Matchers.anyString())).thenReturn(Boolean.FALSE);
	// user.setMasterMid(AppConstants.DRWP_MASTER_MID);
	// user.setUserDirectory(UserDirectory.LDAP.name());
	// ArgumentCaptor<String> loginAuditStatus =
	// ArgumentCaptor.forClass(String.class);
	// ArgumentCaptor<String> loginAuditMessage =
	// ArgumentCaptor.forClass(String.class);
	// try {
	// loginService.loginUser(USERNAME, PASSWORD, CLIENT_APPLICATION_ID,
	// REQUEST_URL);
	// Assert.fail("Should have failed");
	// } catch (AuthenticationException x) {
	// final String jsonResponse = x.getResponse().getEntity().toString();
	// checkResponse(jsonResponse, "DRWP users cannot login from outside of DRWP
	// network");
	// }
	// Mockito.verify(dataAccessService,
	// Mockito.never()).updateUser(Matchers.any());
	// Mockito.verify(ldapAuthnService,
	// Mockito.never()).authenticateUser(Matchers.anyString(),
	// Matchers.anyString());
	// Mockito.verify(dataAccessService,
	// Mockito.times(1)).storeLoginAudit(Matchers.anyString(),
	// Matchers.anyString(),
	// loginAuditMessage.capture(), loginAuditStatus.capture(),
	// Matchers.any());
	// Mockito.verify(addressValidator,
	// Mockito.times(1)).isValidIpAddress(Matchers.anyString(),
	// Matchers.anyString());
	// assertEquals(AppConstants.LOGIN_AUDIT_FAILED,
	// loginAuditStatus.getValue());
	// assertEquals("Login from outside DRWP network",
	// loginAuditMessage.getValue());
	// }
	//
	// @Test
	// public void loginUserTestSuccess_WithLDAP_NonDRWP_User() throws
	// URISyntaxException {
	// ArgumentCaptor<String> loginAudit =
	// ArgumentCaptor.forClass(String.class);
	// final LoginResponse response = loginService.loginUser(USERNAME, PASSWORD,
	// CLIENT_APPLICATION_ID, REQUEST_URL);
	// assertTrue(response.isLastLoginLeg());
	// assertEquals("http://pcc.drwp.com:8080/?code=test1234567&tx=10101000",
	// response.getUri().toString());
	// ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
	// Mockito.verify(dataAccessService,
	// Mockito.times(2)).updateUser(userCaptor.capture());
	// Mockito.verify(ldapAuthnService,
	// Mockito.never()).authenticateUser(Matchers.anyString(),
	// Matchers.anyString());
	// assertTrue(userCaptor.getValue().getIsActiveUser());
	// assertNotNull(userCaptor.getValue().getPasswordExpirationTimestamp());
	// assertNotNull(userCaptor.getValue().getLastLoginAttempt());
	// assertTrue(!userCaptor.getValue().getIsLockedOut());
	// assertEquals(0,
	// userCaptor.getValue().getFailedLoginAttempts().intValue());
	// Mockito.verify(dataAccessService,
	// Mockito.times(1)).storeLoginAudit(Matchers.anyString(),
	// Matchers.anyString(),
	// Matchers.anyString(), loginAudit.capture(),
	// Matchers.any());
	// assertEquals(AppConstants.LOGIN_AUDIT_SUCCESS, loginAudit.getValue());
	// }
	//
	// @Test
	// public void loginUserTestSuccess_InternalDir_DRWP_User() throws
	// URISyntaxException {
	// user.setMasterMid(AppConstants.DRWP_MASTER_MID);
	// ArgumentCaptor<String> loginAudit =
	// ArgumentCaptor.forClass(String.class);
	// final LoginResponse loginResponse = loginService.loginUser(USERNAME,
	// PASSWORD, CLIENT_APPLICATION_ID, REQUEST_URL);
	// assertEquals("http://pcc.drwp.com:8080/?code=test1234567&tx=10101000",
	// loginResponse.getUri().toString());
	// assertTrue(loginResponse.isLastLoginLeg());
	// ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
	// Mockito.verify(dataAccessService,
	// Mockito.times(2)).updateUser(userCaptor.capture());
	// Mockito.verify(ldapAuthnService,
	// Mockito.never()).authenticateUser(Matchers.anyString(),
	// Matchers.anyString());
	// assertTrue(userCaptor.getValue().getIsActiveUser());
	// assertNotNull(userCaptor.getValue().getPasswordExpirationTimestamp());
	// assertNotNull(userCaptor.getValue().getLastLoginAttempt());
	// assertTrue(!userCaptor.getValue().getIsLockedOut());
	// assertEquals(0,
	// userCaptor.getValue().getFailedLoginAttempts().intValue());
	// Mockito.verify(dataAccessService,
	// Mockito.times(1)).storeLoginAudit(Matchers.anyString(),
	// Matchers.anyString(),
	// Matchers.anyString(), loginAudit.capture(),
	// Matchers.any());
	// assertEquals(AppConstants.LOGIN_AUDIT_SUCCESS, loginAudit.getValue());
	// }
	//
	// @Test
	// public void getReturnUrl_WithQuestionMark() {
	// String returnUrl =
	// LoginService.getReturnUrl("http://localhost:9248/api/gateway/loginredirect?to=/cdn/dashboard/index.html",
	// CODE);
	// Assert.assertEquals("http://localhost:9248/api/gateway/loginredirect?code=test1234567&to=/cdn/dashboard/index.html",
	// returnUrl);
	// }
	//
	// @Test
	// public void getReturnUrl_WithoutQuestionMark() {
	// String returnUrl =
	// LoginService.getReturnUrl("http://localhost:9248/api/gateway/loginredirect",
	// CODE);
	// Assert.assertEquals("http://localhost:9248/api/gateway/loginredirect?code=test1234567",
	// returnUrl);
	// }
	//
	// @Test
	// public void getReturnUrl_WithQuestionMark_AndWithHash() {
	// String returnUrl =
	// LoginService.getReturnUrl("http://localhost:9248/api/gateway/loginredirect?to=/cdn/dashboard/index.html#/security/systemusers",
	// CODE);
	// Assert.assertEquals("http://localhost:9248/api/gateway/loginredirect?code=test1234567&to=/cdn/dashboard/index.html#/security/systemusers",
	// returnUrl);
	// }
	//
	// @Test
	// public void getReturnUrl_WithoutQuestionMark_AndWithHash() {
	// String returnUrl =
	// LoginService.getReturnUrl("http://localhost:9248/api/gateway/loginredirect#/security/systemusers",
	// CODE);
	// Assert.assertEquals("http://localhost:9248/api/gateway/loginredirect#/security/systemusers?code=test1234567",
	// returnUrl);
	// }
}
