package com.izettle.assignment.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.Date;
import org.apache.commons.configuration.ConfigurationException;
import org.junit.After;
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
	private LoginAudit loginAudit;

	@Before
	public void setUp() throws ConfigurationException {
		createTestUser();
		createTestUserDisplay();
		createTestBearerToken();
		loginAudit = LoginAudit.createInstance(USERNAME, AppConstants.OK, AppConstants.OK, BEARER, Boolean.TRUE);
		loginService = new LoginService(issuedBearerTokenDao, loginAuditsDao, userDao, bearerRandomGenerator,
				passwordCrypto);
		when(userDao.getUserByUsername(USERNAME)).thenReturn(user);
		when(passwordCrypto.generateSalt()).thenReturn(IzettleUtils.decodeFromBase64Bytes(SALT));

		Mockito.doNothing().when(userDao).storeUser(user);
		Mockito.doNothing().when(issuedBearerTokenDao).createBearerTokens(issuedBearerToken);
		Mockito.doNothing().when(loginAuditsDao).store(loginAudit);

	}
	
	@After
	public void reset() {
		Mockito.reset(userDao);
		Mockito.reset(passwordCrypto);
		Mockito.reset(issuedBearerTokenDao);
		Mockito.reset(loginAuditsDao);
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
		ArgumentCaptor.forClass(User.class);
		ArgumentCaptor.forClass(LoginAudit.class);

		loginService.verifyLoginIn(USERNAME, "adfafafafaf");
		
		Mockito.verify(userDao, Mockito.times(1)).getUserByUsername(WRONG_PASS);

	}

//	@Test
//	public void verifySuccess() {
//		when(userDao.checkIfUserExists(USERNAME)).thenReturn(Boolean.FALSE);
//	
//		ArgumentCaptor<LoginAudit> loginAuditCapture = ArgumentCaptor.forClass(LoginAudit.class);
//		ArgumentCaptor<IssuedBearerToken> tokenCapture = ArgumentCaptor.forClass(IssuedBearerToken.class);
//
//		loginService.verifyLoginIn(USERNAME, PASSWORD);
//
//		Mockito.verify(issuedBearerTokenDao, Mockito.times(1)).createBearerTokens(tokenCapture.capture());
//		Mockito.verify(loginAuditsDao, Mockito.times(1)).store(loginAuditCapture.capture());
//		
//		assertEquals(USERNAME, loginAuditCapture.getValue().getUserName());
//		//assertEquals(BEARER, loginAuditCapture.getValue().getBearer());
//		assertTrue(!loginAuditCapture.getValue().getIsSucess());
//		assertNotNull(loginAuditCapture.getValue().getRequestTimestamp());
//
//	}
	

	private Timestamp getNowTimestamp() {
		return new Timestamp(new Date().getTime());
	}

	private Timestamp getExpirationTimestamp() {
		return new Timestamp(new Date().getTime() + AppConstants.TOKEN_EXP_MS);
	}

}
