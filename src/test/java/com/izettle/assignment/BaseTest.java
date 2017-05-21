package com.izettle.assignment;
/// *
// * Copyright 2014 Digital River World Payments AB.
// */
// package com.drwp.securityservice.authentication;
//
// import static org.junit.Assert.assertEquals;
// import static org.junit.Assert.assertNotNull;
// import static org.junit.Assert.assertTrue;
//
// import java.io.InputStream;
// import java.lang.reflect.Type;
// import java.net.URL;
// import java.sql.Timestamp;
// import java.util.Arrays;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
//
// import javax.ws.rs.core.NewCookie;
// import javax.ws.rs.core.Response;
// import javax.ws.rs.core.Response.Status;
//
// import org.apache.commons.codec.net.URLCodec;
// import org.apache.commons.configuration.ConfigurationException;
// import org.apache.commons.configuration.PropertiesConfiguration;
// import org.apache.commons.io.IOUtils;
// import org.apache.log4j.PropertyConfigurator;
// import org.joda.time.DateTime;
// import org.junit.Assert;
// import org.junit.Before;
// import org.junit.BeforeClass;
// import org.mockito.Mockito;
//
// import com.datastax.driver.core.Session;
// import com.drwp.securityservice.authentication.dao.ApplicationDao;
// import com.drwp.securityservice.authentication.dao.AuthorizationCodeDao;
// import com.drwp.securityservice.authentication.dao.UserDao;
// import com.drwp.securityservice.authentication.entity.Application;
// import com.drwp.securityservice.authentication.entity.AuthorizationCode;
// import com.drwp.securityservice.authentication.entity.User;
// import com.drwp.securityservice.authentication.service.DataAccessService;
// import com.drwp.securityservice.authentication.utils.CassandraSessionFactory;
// import com.google.gson.Gson;
// import com.google.gson.GsonBuilder;
// import com.google.gson.reflect.TypeToken;
// import com.yubico.u2f.U2F;
// import com.yubico.u2f.data.DeviceRegistration;
// import com.yubico.u2f.data.messages.AuthenticateRequest;
// import com.yubico.u2f.data.messages.AuthenticateRequestData;
//
// public class BaseTest {
//
// protected static int AUTHZCODE_TTL = 864000;
// protected static final String XSRF_COOKIE_NAME = "XSRF-AUTHN-TOKEN";
// protected static final String BEARER_TOKEN_PARAM = "bearer_token";
// protected static final String REFRESH_TOKEN_PARAM = "refresh_token";
// protected static final String CLIENT_SECRET_PARAM = "client_secret";
// protected static final String CLIENT_ID_PARAM = "client_id";
// protected static final String AUTH_CODE_PARAM = "code";
// protected final static String CLIENT_APPLICATION_ID = "PCC";
// protected final static String CLIENT_APPLICATION_SECRET = "skdf2893rfi3h9gf3foihf93gf";
// protected final static String CLIENT_APPLICATION_URL =
/// "http://localhost:9249/api/gateway/loginredirect?to=/cdn/dashboard/index.html#/dashboard.html";
// protected final static String CLIENT_APPLICATION_NAME = "pcc";
// protected final static String CREATED_BY = "edrin";
// protected final static String UPDATED_BY = "sikander";
// protected final static Long BEARER_EXP_SECONDS = 10L;
// protected final static Long REFRESH_EXP_SECONDS = 100L;
// protected static final String MASTER_MID = "SPOTIFY";
// protected static final String USER_NAME = "sgrewal@digitalriver.com";
// protected static final String USER_NAME2 = "egjoleka@digitalriver.com";
// protected static final Timestamp createdTimestamp = new Timestamp(new DateTime().getMillis());
// protected static final Timestamp updatedTimestamp = new Timestamp(new DateTime().getMillis() + 1000000);
// protected final static String SALT = "F7BYG9wqegE=";
// protected final static String PASS_ENC = "o42C+QUi9rvRq3kELeC0cAVkEDk=";
// protected static final String FIRST_NAME = "edrin";
// protected static final String LAST_NAME = "gjoleka";
// protected static final Integer FAILED_LOGIN_ATTEMP = 1;
// protected static final String MOBILE_NUMBER = "0733312529";
// protected static final String AUTHZ_CODE = "tsakjhwi3uhfwsdfsssest1234567";
// protected final static Long BEARER_EXPIRATION = 30L;
// protected final static Long REFRESH_EXPIRATION = 300L;
//
// protected final static String U2F_CHALLENGE = "QGJFGI5YLd-Isp8U4fjnD_9330BXdhy3YwQpwPv32aU";
// protected final static String U2F_APP_ID = "https://localhost:9236";
// protected final static String U2F_KEY_HANDLE =
/// "5aUFki8gG8ptSBfMxuqTfgCnSdQAR7whQOBkdX36BUMiKK_PreR-PGNOJBVWZgNAU_snz7n3waEgM77U048-Dg";
// protected final static String U2F_PUBLIC_KEY =
/// "BKnfpZyKK7LU08W0AcjVJrnt7cvSAXEnj0GCKnEcq06-xkyrKzT86OMDRi9m0zgScZWOlgpfn1lGFmyXDyrRoY8";
// protected final static String U2F_SIGNATURE_DATA =
/// "AQAAAFAwRQIgBkSotlqyRCpmNiR1RYMqpjpNNJ0-lCSK7eeeVehi2A0CIQDDz-HHD9RCqlQSFmsn5SXjZsmkJn66c0OOu-UPfPiCdQ";
// protected final static String U2F_TOK_RESP_CLIENT_DATA =
/// "eyJ0eXAiOiJuYXZpZ2F0b3IuaWQuZ2V0QXNzZXJ0aW9uIiwiY2hhbGxlbmdlIjoiUUdKRkdJNVlMZC1Jc3A4VTRmam5EXzkzMzBCWGRoeTNZd1Fwd1B2MzJhVSIsIm9yaWdpbiI6Imh0dHBzOi8vbG9jYWxob3N0OjkyMzYiLCJjaWRfcHVia2V5IjoidW51c2VkIn0";
//
// protected static final String ISO_DATETIME_TIME_ZONE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZZ";
// protected static final Gson gson = new GsonBuilder().setDateFormat(ISO_DATETIME_TIME_ZONE_FORMAT).create();
// protected static DataAccessService dataAccessService;
// protected static ApplicationDao applicationDao;
// protected static AuthorizationCodeDao authorizationCodeDao;
// protected static UserDao userDao;
// protected static JunitDao junitDao;
// protected static PropertiesConfiguration cfg;
//
// @SuppressWarnings("resource")
// @BeforeClass
// public static void beforeClass() throws ConfigurationException {
// final String filePath = BaseTest.class.getClassLoader().getResource("test.properties").getFile();
// cfg = new PropertiesConfiguration(filePath);
// PropertyConfigurator.configure(filePath);
// Session session = CassandraSessionFactory.getSession();
// applicationDao = new ApplicationDao(session);
// authorizationCodeDao = new AuthorizationCodeDao(session);
//
// }
//
// @Before
// public void cleanUp() {
// junitDao.wipeData();
// }
//
// protected static Session getSession() {
// return CassandraSessionFactory.getSession();
// }
//
// protected static String getStringResponse(final Response response) {
// try {
// return IOUtils.toString((InputStream) response.getEntity());
// } catch (Exception e) {
// throw new RuntimeException(e);
// }
// }
//
// protected User createUser() {
// return createUser(USER_NAME, Boolean.TRUE);
// }
//
// protected User createUser(final String userName, final boolean isActive) {
// final User user = new User();
// user.setUserName(userName);
// user.setIsActiveUser(isActive);
// user.setIsEncryptionEnabled(Boolean.TRUE);
// user.setIsTwoLeggedUser(Boolean.FALSE);
// user.setMasterMid(MASTER_MID);
// user.setPassword(PASS_ENC);
// user.setSalt(SALT);
// user.setTwoLeggedBearerAttemps(10);
// user.setTwoLeggedBearerTokenValiditySeconds(3600L);
// user.setUpdatedBy(UPDATED_BY);
// user.setUpdatedTimestamp(updatedTimestamp);
// user.setFirstName(FIRST_NAME);
// user.setLastName(LAST_NAME);
// user.setMobileNumber(MOBILE_NUMBER);
// user.setFailedLoginAttempts(FAILED_LOGIN_ATTEMP);
// user.setLastLoginAttempt(createdTimestamp);
// user.setPasswordExpirationTimestamp(new Timestamp(new DateTime().plusHours(1).getMillis()));
// user.setIsLockedOut(Boolean.FALSE);
// user.setCreatedBy(CREATED_BY);
// user.setCreatedTimestamp(createdTimestamp);
// user.setIsTwoFactorAuthenticationEnabled(Boolean.FALSE);
// user.setIsEmailVerified(Boolean.TRUE);
// return user;
// }
//
// protected Application createApplication() {
// final Application application = new Application();
// application.setBearerExpirationInSeconds(BEARER_EXP_SECONDS);
// application.setClientApplicationId(CLIENT_APPLICATION_ID);
// application.setClientApplicationName(CLIENT_APPLICATION_NAME);
// application.setClientSecretText(CLIENT_APPLICATION_SECRET);
// application.setCreatedBy(CREATED_BY);
// application.setCreatedTimestamp(new Timestamp(new DateTime().getMillis()));
// application.setRefreshTokenExpirationInSeconds(REFRESH_EXP_SECONDS);
// application.setUpdatedBy(UPDATED_BY);
// application.setUpdatedTimestamp(null);
// application.setUriText(CLIENT_APPLICATION_URL);
// application.setMaxNumberOfRefreshTokens(10);
// application.setIsActive(Boolean.TRUE);
// return application;
// }
//
// protected AuthorizationCode createAuthorizationCode() {
// return createAuthorizationCode(AUTHZ_CODE, AuthorizationCodeType.LOGIN, Boolean.TRUE);
// }
//
// protected AuthorizationCode createAuthorizationCode(final String authCode, final AuthorizationCodeType codeType,
// final Boolean isValid) {
// final AuthorizationCode authorizationCode = new AuthorizationCode();
// authorizationCode.setAuthCode(authCode);
// authorizationCode.setIsValidCode(isValid);
// authorizationCode.setAuthzCodeType(codeType);
// authorizationCode.setClientApplicationId(CLIENT_APPLICATION_ID);
// authorizationCode.setClientSecret(CLIENT_APPLICATION_SECRET);
// authorizationCode.setCodeExpirationTime(new Timestamp(new DateTime().getMillis() + 100000000));
// authorizationCode.setCreatedBy(CREATED_BY);
// authorizationCode.setCreatedTimestamp(new Timestamp(new DateTime().getMillis()));
// authorizationCode.setMasterMid(MASTER_MID);
// authorizationCode.setUpdatedBy(UPDATED_BY);
// authorizationCode.setUpdatedTimestamp(new Timestamp(new DateTime().getMillis()));
// authorizationCode.setUserName(USER_NAME);
// return authorizationCode;
// }
//
// protected AuthorizationCode createAuthorizationCodeByUser(final String authCode, final AuthorizationCodeType
/// codeType,
// final Boolean isValid, final String username) {
// final AuthorizationCode authorizationCode = new AuthorizationCode();
// authorizationCode.setAuthCode(authCode);
// authorizationCode.setIsValidCode(isValid);
// authorizationCode.setAuthzCodeType(codeType);
// authorizationCode.setClientApplicationId(CLIENT_APPLICATION_ID);
// authorizationCode.setClientSecret(CLIENT_APPLICATION_SECRET);
// authorizationCode.setCodeExpirationTime(new Timestamp(new DateTime().getMillis() + 100000000));
// authorizationCode.setCreatedBy(CREATED_BY);
// authorizationCode.setCreatedTimestamp(new Timestamp(new DateTime().getMillis()));
// authorizationCode.setMasterMid(MASTER_MID);
// authorizationCode.setUpdatedBy(UPDATED_BY);
// authorizationCode.setUpdatedTimestamp(new Timestamp(new DateTime().getMillis()));
// authorizationCode.setUserName(username);
// return authorizationCode;
// }
//
// protected void assertRedirectRequest(final Response response, final String message) {
// assertRedirectRequest(response, message, "invalid_request");
// }
//
// protected void assertRedirectRequest(final Response response, final String message, final String error) {
// assertRedirectRequest(response);
// final URL location = getLocationHeader(response);
// final String jsonReponse = getJsonErrorResponse(location);
// final Map<String, String> responseAsJsonMap = jsonToMap(jsonReponse);
// assertEquals(error, responseAsJsonMap.get("error"));
// assertEquals(message, responseAsJsonMap.get("error_description"));
// }
//
// private URL getLocationHeader(final Response response) {
// try {
// final String location = response.getHeaderString("location");
// byte[] decodeUrl = URLCodec.decodeUrl(location.getBytes());
// return new URL(new String(decodeUrl));
// } catch (Exception e) {
// throw new RuntimeException(e);
// }
// }
//
// private String getJsonErrorResponse(final URL location) {
// final String query = location.getQuery();
// int startIndex = query.indexOf("{");
// int endIndex = query.indexOf("}", startIndex);
// return query.substring(startIndex, endIndex + 1);
// }
//
// private void assertRedirectRequest(final Response response) {
// assertEquals(Status.SEE_OTHER.getStatusCode(), response.getStatus());
// assertEquals(Status.SEE_OTHER.getReasonPhrase(), response.getStatusInfo().getReasonPhrase());
// }
//
// protected <T> T getResponse(final Response response, final Class<T> clz) {
// try {
// final String jsonResponse = IOUtils.toString((InputStream) response.getEntity());
// return gson.fromJson(jsonResponse, clz);
// } catch (final Exception ex) {
// throw new RuntimeException(ex);
// }
// }
//
// protected Map<String, String> responseToMap(final Response response) {
// final Type type = new TypeToken<Map<String, String>>() {}.getType();
// return gson.fromJson(getStringResponse(response), type);
// }
//
// private Map<String, String> jsonToMap(final String jsonResponse) {
// final Type type = new TypeToken<Map<String, String>>() {}.getType();
// return gson.fromJson(jsonResponse, type);
// }
//
// protected String parseAuthCodeFromMailBody(final String mailBody) {
// int startIndex = mailBody.indexOf("?code=");
// int endIndex = mailBody.indexOf("\"", startIndex);
// return mailBody.substring(startIndex + 6, endIndex);
// }
//
// protected void assertBadRequest(final Response response, final String message) {
// assertBadRequest(response);
// final Map<String, String> responseAsJsonMap = jsonToMap(getStringResponse(response));
// assertEquals("invalid_request", responseAsJsonMap.get("error"));
// assertEquals(message, responseAsJsonMap.get("error_description"));
// }
//
// protected void assertServerError(final Response response, final String message) {
// assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
// assertEquals(Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), response.getStatusInfo().getReasonPhrase());
// final Map<String, String> responseAsJsonMap = jsonToMap(getStringResponse(response));
// assertEquals("server_error", responseAsJsonMap.get("error"));
// assertEquals(message, responseAsJsonMap.get("error_description"));
// }
//
// protected void assertBadRequestExceptionResponse(final Response response, final String messsage) {
// assertBadRequest(response);
// final Map<String, String> responseAsJsonMap = jsonToMap((String) response.getEntity());
// assertEquals("invalid_request", responseAsJsonMap.get("error"));
// assertEquals(messsage, responseAsJsonMap.get("error_description"));
// }
//
// private void assertBadRequest(final Response response) {
// assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
// assertEquals(Status.BAD_REQUEST.getReasonPhrase(), response.getStatusInfo().getReasonPhrase());
// }
//
// }
