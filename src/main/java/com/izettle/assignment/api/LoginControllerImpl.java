package com.izettle.assignment.api;

import static com.izettle.assignment.utils.IzettleUtils.requireNonBlank;
import static com.izettle.assignment.utils.IzettleUtils.validateEmail;
import static com.izettle.assignment.utils.IzettleUtils.validateLength;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.cxf.interceptor.InInterceptors;
import org.apache.cxf.interceptor.OutInterceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.izettle.assignment.AppConstants;
import com.izettle.assignment.IzettleAssignmentMain;
import com.izettle.assignment.ddo.BearerTokenDisplayEntity;
import com.izettle.assignment.ddo.UserDisplayEntity;
import com.izettle.assignment.service.LoginService;

@InInterceptors(interceptors = "com.izettle.assignment.interceptors.IZettleServiceLoggingInInterceptor")
@OutInterceptors(interceptors = "com.izettle.assignment.interceptors.IZettleServiceLoggingOutInterceptor")
public class LoginControllerImpl implements LoginController {

	final LoginService loginService;

	private static final Logger cLogger = LoggerFactory.getLogger(LoginControllerImpl.class);

	public LoginControllerImpl(final LoginService loginService) {
		this.loginService = loginService;
	}

	@Override
	public String isAlive() {
		cLogger.info("Trying to check the status.");
		if (IzettleAssignmentMain.txGate.isOpen() && loginService.isCassandraAccessible()) {
			return AppConstants.STATUS_UP;
		}
		return AppConstants.STATUS_DOWN;
	}

	@Override
	public Response signIn(final String username, final String password, final HttpServletRequest httpServletRequest,
			final HttpServletResponse httpServletResponse) {
		cLogger.info("AUDIT--User {} is trying to login", username);
		validateSignInParameters(username);
		requireNonBlank(password, "password");
		validateLength(password, "password", 160);
		final BearerTokenDisplayEntity bearerTokenDisplayEntity = loginService.verifyLoginIn(username, password);
		return Response.ok().entity(bearerTokenDisplayEntity).build();

	}

	@Override
	public Response registerUser(final UserDisplayEntity userDisplayEntity, HttpServletResponse httpServletResponse) {
		loginService.registerUser(userDisplayEntity);
		return Response.status(Status.CREATED).build();
	}

	private void validateSignInParameters(final String username) {
		requireNonBlank(username, "user");
		validateLength(username, "user", 100);
		validateEmail(username);
	}

}
