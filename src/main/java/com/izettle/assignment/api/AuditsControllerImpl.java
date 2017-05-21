package com.izettle.assignment.api;

import static com.izettle.assignment.utils.IzettleUtils.requireNonBlank;

import org.apache.cxf.interceptor.InInterceptors;
import org.apache.cxf.interceptor.OutInterceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.izettle.assignment.ddo.AuditsDisplayEntity;
import com.izettle.assignment.service.AuditsService;

@InInterceptors(interceptors = "com.izettle.assignment.interceptors.IZettleServiceLoggingInInterceptor")
@OutInterceptors(interceptors = "com.izettle.assignment.interceptors.IZettleServiceLoggingOutInterceptor")
public class AuditsControllerImpl implements AuditsController {

	final AuditsService auditsService;

	private static final Logger logger = LoggerFactory.getLogger(AuditsControllerImpl.class);

	public AuditsControllerImpl(final AuditsService auditsService) {
		this.auditsService = auditsService;
	}

	@Override
	public AuditsDisplayEntity getAudits(String bearer, boolean isSuccess) {
		validateSignInParameters(bearer, isSuccess);
		logger.info("Got a request for Audits for token: {}", bearer.substring(0, 4));
		return auditsService.getAuditsDisplayEntityForBearerToken(bearer, isSuccess);
	}

	private void validateSignInParameters(final String bearer, final boolean isSuccess) {
		requireNonBlank(bearer, "bearer");
		requireNonBlank(bearer, "isSuccess");

	}

}
