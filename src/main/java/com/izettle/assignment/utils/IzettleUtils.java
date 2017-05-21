package com.izettle.assignment.utils;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;

public class IzettleUtils {
	public static void requireNonNull(final Object value, final String fieldName) {
		if (value == null) {
			ExceptionCreator.throwBadRequestException(fieldName + " cannot be null");
		}
	}

	public static void requireNonEmpty(final Collection<?> collection, final String fieldName) {
		if (collection == null || collection.isEmpty()) {
			ExceptionCreator.throwBadRequestException(fieldName + " cannot be null or empty");
		}
	}

	public static void requireNonBlank(final String value, final String fieldName) {
		if (StringUtils.isBlank(value)) {
			ExceptionCreator.throwBadRequestException(fieldName + " cannot be null or empty");
		}
	}

	public static void validateLength(final String value, final String fieldName, final int maxlength) {
		if (value.length() > maxlength) {
			ExceptionCreator.throwBadRequestException(fieldName + " exceeds max permitted length!");
		}
	}

	private static HttpServletRequest getHttpServletRequest() {
		final Message message = PhaseInterceptorChain.getCurrentMessage();
		if (message != null) {
			return (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
		}
		return null;
	}

	private static String getRequestHeader(final String headerName) {
		final HttpServletRequest request = getHttpServletRequest();
		if (request != null) {
			return request.getHeader(headerName);
		}
		return null;
	}

	public static String getClientIpAddress() {
		final HttpServletRequest request = getHttpServletRequest();
		String ipAddress = null;
		if (request != null) {
			ipAddress = request.getHeader("CLIENTIP");
			if (ipAddress == null) {
				ipAddress = request.getRemoteAddr();
			}
		}
		return ipAddress;
	}

	public static UserAgent getUserAgent() {
		return new UserAgent(getUserAgentHeader());
	}

	public static String getUserAgentHeader() {
		return getRequestHeader("User-Agent");
	}

	public static String getBrowserInfo(final UserAgent userAgent) {
		final Browser browser = userAgent.getBrowser() != null ? userAgent.getBrowser() : Browser.UNKNOWN;
		if (Browser.DOWNLOAD.equals(browser)) {
			return "Client Library";
		}
		final Version browserVersion = userAgent.getBrowserVersion() != null ? userAgent.getBrowserVersion()
				: new Version("UNKNOWN", "", "");
		return browser.getGroup().getName() + "-" + browserVersion.getVersion();
	}

	public static String getOperatingSystem(final UserAgent userAgent) {
		final OperatingSystem operatingSystem = userAgent.getOperatingSystem() != null ? userAgent.getOperatingSystem()
				: OperatingSystem.UNKNOWN;
		return operatingSystem.getName();
	}

	public static String getDeviceType(final UserAgent userAgent) {
		final OperatingSystem operatingSystem = userAgent.getOperatingSystem() != null ? userAgent.getOperatingSystem()
				: OperatingSystem.UNKNOWN;
		return operatingSystem.getDeviceType().getName();
	}

	public static void validateEmail(final String userName) {
		final EmailValidator validator = EmailValidator.getInstance();
		if (!validator.isValid(userName)) {
			ExceptionCreator.throwBadRequestException("UserName '" + userName + "' is not a valid email address");
		}
	}

	public static ResponseBuilder addHeaders(final ResponseBuilder responseBuilder) {
		return responseBuilder.header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
				.header("Pragma", "no-cache").header("X-Frame-Options", "DENY")
				.header("X-XSS-Protection", "1; mode=block").header("X-Content-Type-Options", "nosniff");
	}

	public static String convertBytesToBase64String(final byte[] source) {
		return Base64.encodeBase64String(source);
	}

	public static byte[] decodeFromBase64Bytes(final String encoded) {
		return Base64.decodeBase64(encoded);
	}

}
