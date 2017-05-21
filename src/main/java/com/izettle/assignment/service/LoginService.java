package com.izettle.assignment.service;

import static com.izettle.assignment.utils.ArgumentVerifier.verifyNotNull;
import static com.izettle.assignment.utils.ExceptionCreator.throwBadRequestException;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.izettle.assignment.AppConstants;
import com.izettle.assignment.crypto.PasswordCrypto;
import com.izettle.assignment.dao.IssuedBearerTokenDao;
import com.izettle.assignment.dao.LoginAuditsDao;
import com.izettle.assignment.dao.UserDao;
import com.izettle.assignment.ddo.BearerTokenDisplayEntity;
import com.izettle.assignment.ddo.UserDisplayEntity;
import com.izettle.assignment.entity.IssuedBearerToken;
import com.izettle.assignment.entity.LoginAudit;
import com.izettle.assignment.entity.User;
import com.izettle.assignment.exception.IzettleException;
import com.izettle.assignment.utils.BearerRandomGenerator;
import com.izettle.assignment.utils.IzettleUtils;

public class LoginService {

	private static final Logger logger = LoggerFactory.getLogger(LoginService.class);
	private static final String INVALID_LOGIN = "Invalid username or password";
	private IssuedBearerTokenDao issuedBearerTokenDao;
	private LoginAuditsDao loginAuditsDao;
	private UserDao userDao;
	private BearerRandomGenerator tokenBearerGenerator;
	private PasswordCrypto passwordCrypto;

	public LoginService(final IssuedBearerTokenDao issuedBearerTokenDao, final LoginAuditsDao loginAuditsDao,
			final UserDao userDao, final BearerRandomGenerator tokenBearerGenerator,
			final PasswordCrypto passwordCrypto) {
		verifyNotNull(issuedBearerTokenDao);
		verifyNotNull(loginAuditsDao);
		verifyNotNull(userDao);
		verifyNotNull(tokenBearerGenerator);
		verifyNotNull(passwordCrypto);
		this.issuedBearerTokenDao = issuedBearerTokenDao;
		this.loginAuditsDao = loginAuditsDao;
		this.userDao = userDao;
		this.tokenBearerGenerator = tokenBearerGenerator;
		this.passwordCrypto = passwordCrypto;
	}

	public boolean isCassandraAccessible() {
		try {
			issuedBearerTokenDao.validateCassandraAccess();
			return true;
		} catch (RuntimeException ex) {
			logger.error("Cannot access Cassandra", ex);
			return false;
		}
	}

	public void registerUser(final UserDisplayEntity userDisplayEntity) {
		if (userDao.checkIfUserExists(userDisplayEntity.getUserName())) {
			throwBadRequestException(
					"The user already exists, please choose a different username: " + userDisplayEntity.getUserName());
		}
		final User user = new User();
		PasswordCrypto.validatePasswordPolicy(userDisplayEntity.getUserName(), userDisplayEntity.getPassword(),
				userDisplayEntity.getConfirmedPassword());
		user.setFirstName(userDisplayEntity.getFirstName());
		user.setLastName(userDisplayEntity.getLastName());
		user.setIsActiveUser(Boolean.TRUE);
		user.setCreatedTimestamp(getNowTimestamp());
		user.setUserName(userDisplayEntity.getUserName());
		user.setSalt(IzettleUtils.convertBytesToBase64String(passwordCrypto.generateSalt()));
		user.setPassword(PasswordCrypto.getEncryptedPassword(userDisplayEntity.getPassword(),
				IzettleUtils.decodeFromBase64Bytes(user.getSalt())));

		userDao.storeUser(user);
	}

	public BearerTokenDisplayEntity verifyLoginIn(final String username, final String password) {
		final User user = userDao.getUserByUsername(username);
		byte[] salt = IzettleUtils.decodeFromBase64Bytes(user.getSalt());
		final byte[] encryptedPassword = IzettleUtils.decodeFromBase64Bytes(user.getPassword());
		if (verifyPassword(password, encryptedPassword, salt)) {
			final String uniqueToken = tokenBearerGenerator.generateValue();
			IssuedBearerToken issuedBearerToken = new IssuedBearerToken(uniqueToken, getExpirationTimestamp(),
					Boolean.TRUE, username, getNowTimestamp());
			issuedBearerTokenDao.createBearerTokens(issuedBearerToken);
			final LoginAudit successAudit = LoginAudit.createInstance(username, AppConstants.LOGIN_AUDIT_SUCCESS,
					AppConstants.OK, uniqueToken, Boolean.TRUE);
			loginAuditsDao.store(successAudit);
			return new BearerTokenDisplayEntity(uniqueToken, issuedBearerToken.getExpirationTime());
		}
		final LoginAudit successAudit = LoginAudit.createInstance(username, AppConstants.LOGIN_AUDIT_FAILED,
				INVALID_LOGIN, INVALID_LOGIN, Boolean.FALSE);
		loginAuditsDao.store(successAudit);
		throw new IzettleException(Response.status(Status.UNAUTHORIZED).entity(INVALID_LOGIN).build());
	}

	private Timestamp getNowTimestamp() {
		return new Timestamp(new Date().getTime());
	}

	private Timestamp getExpirationTimestamp() {
		return new Timestamp(new Date().getTime() + AppConstants.TOKEN_EXP_MS);
	}

	private boolean verifyPassword(final String attemptedPassword, final byte[] encryptedPassword, final byte[] salt) {
		// Encrypt the clear-text password using the same salt that was used to
		// encrypt the original password
		byte[] encryptedAttemptedPassword = PasswordCrypto.getEncryptedPasswordByteArray(attemptedPassword, salt);

		// Authentication succeeds if encrypted password that the user entered
		// is equal to the stored pbdfk2
		return Arrays.equals(encryptedPassword, encryptedAttemptedPassword);
	}

}
