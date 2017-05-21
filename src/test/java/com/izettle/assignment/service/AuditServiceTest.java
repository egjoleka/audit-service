package com.izettle.assignment.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.BadRequestException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.datastax.driver.core.utils.UUIDs;
import com.izettle.assignment.AppConstants;
import com.izettle.assignment.dao.IssuedBearerTokenDao;
import com.izettle.assignment.dao.LoginAuditsDao;
import com.izettle.assignment.ddo.AuditsDisplayEntity;
import com.izettle.assignment.entity.IssuedBearerToken;
import com.izettle.assignment.entity.LoginAudit;

@RunWith(MockitoJUnitRunner.class)
public class AuditServiceTest {
	private IssuedBearerTokenDao issuedBearerTokenDao = Mockito.mock(IssuedBearerTokenDao.class);
	private LoginAuditsDao loginAuditsDao = Mockito.mock(LoginAuditsDao.class);
	final IssuedBearerToken issuedBearerToken = new IssuedBearerToken();
	final LoginAudit loginAudit = new LoginAudit();
	private static final String BEARER = "111111111111111111111111";
	private static final String USERNAME = "gjolekae@gmail.com";
	private final List<LoginAudit> audits = new ArrayList<>();
	private AuditsService auditsService;

	@Before
	public void warmUp() {
		createTestBearerToken();
		createLoginAudits();
		Mockito.when(issuedBearerTokenDao.getBearerTokensByBearerTokenId(BEARER)).thenReturn(issuedBearerToken);
		Mockito.when(loginAuditsDao.getLoginAuditsByUsernameAndStatus(USERNAME, Boolean.TRUE)).thenReturn(audits);
		auditsService = new AuditsService(issuedBearerTokenDao, loginAuditsDao);
	}

	protected void createTestBearerToken() {
		issuedBearerToken.setBearerTs(UUIDs.timeBased());
		issuedBearerToken.setCreatedTimestamp(getNowTimestamp());
		issuedBearerToken.setExpirationTime(getExpirationTimestamp());
		issuedBearerToken.setIssuedBearerToken(BEARER);
		issuedBearerToken.setIsValidBearer(Boolean.TRUE);
		issuedBearerToken.setUserName(USERNAME);
	}

	protected void createLoginAudits() {
		audits.add(LoginAudit.createInstance(USERNAME, AppConstants.OK, AppConstants.OK, BEARER, Boolean.TRUE));
		audits.add(LoginAudit.createInstance(USERNAME, AppConstants.OK, AppConstants.OK, BEARER + "2", Boolean.TRUE));
	}

	@Test
	public void testGetAuditsByBearerTokenSuccess() {
		final AuditsDisplayEntity auditsDisplay = auditsService.getAuditsDisplayEntityForBearerToken(BEARER,
				Boolean.TRUE);

		Assert.assertEquals(2, auditsDisplay.getLoginAudits().size());
		Assert.assertEquals(BEARER, auditsDisplay.getLoginAudits().get(0).getBearer());
		Assert.assertEquals(Boolean.TRUE, auditsDisplay.getLoginAudits().get(0).getIsSucess());
		Assert.assertEquals(AppConstants.OK, auditsDisplay.getLoginAudits().get(0).getReason());
		Assert.assertEquals(AppConstants.OK, auditsDisplay.getLoginAudits().get(0).getStatus());
		Assert.assertEquals(USERNAME, auditsDisplay.getLoginAudits().get(0).getUserName());

	}

	@Test(expected = BadRequestException.class)
	public void testGetAuditsByWrongBearerTokenFailure() {
		Mockito.when(issuedBearerTokenDao.getBearerTokensByBearerTokenId("BEARER"))
				.thenThrow(new BadRequestException());

		auditsService.getAuditsDisplayEntityForBearerToken("BEARER", Boolean.TRUE);
	}

	private Timestamp getNowTimestamp() {
		return new Timestamp(new Date().getTime());
	}

	private Timestamp getExpirationTimestamp() {
		return new Timestamp(new Date().getTime() + AppConstants.TOKEN_EXP_MS);
	}
}
