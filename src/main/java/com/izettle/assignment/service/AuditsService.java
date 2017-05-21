package com.izettle.assignment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.izettle.assignment.dao.IssuedBearerTokenDao;
import com.izettle.assignment.dao.LoginAuditsDao;
import com.izettle.assignment.ddo.AuditsDisplayEntity;
import com.izettle.assignment.entity.IssuedBearerToken;

public class AuditsService {

	private static final Logger logger = LoggerFactory.getLogger(AuditsService.class);
	private IssuedBearerTokenDao issuedBearerTokenDao;
	private LoginAuditsDao loginAuditsDao;
	
	public AuditsService(IssuedBearerTokenDao issuedBearerTokenDao, LoginAuditsDao loginAuditsDao) {
		this.issuedBearerTokenDao = issuedBearerTokenDao;
		this.loginAuditsDao = loginAuditsDao;
	}
	
	public AuditsDisplayEntity getAuditsDisplayEntityForBearerToken(final String bearerToken, final Boolean isSuccess) {
		final IssuedBearerToken issuedBearerToken = issuedBearerTokenDao.getBearerTokensByBearerTokenId(bearerToken);
		final String userName = issuedBearerToken.getUserName();
		logger.info("Trying to grab the audits for user: {}", userName);
		return new AuditsDisplayEntity(loginAuditsDao.getLoginAuditsByUsernameAndStatus(userName, isSuccess));
	}
	
	
	
}
