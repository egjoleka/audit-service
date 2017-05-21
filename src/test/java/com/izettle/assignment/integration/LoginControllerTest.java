package com.izettle.assignment.integration;
// package com.drwp.securityservice.authentication.integration;
//
// import java.io.InputStream;
// import java.sql.Timestamp;
//
// import javax.ws.rs.core.MediaType;
// import javax.ws.rs.core.NewCookie;
// import javax.ws.rs.core.Response;
// import javax.ws.rs.core.Response.Status;
//
// import org.apache.cxf.endpoint.Server;
// import org.apache.cxf.jaxrs.client.WebClient;
// import org.apache.cxf.jaxrs.ext.form.Form;
// import org.eclipse.jetty.http.HttpHeaders;
// import org.joda.time.DateTime;
// import org.junit.AfterClass;
// import org.junit.Assert;
// import org.junit.Before;
// import org.junit.BeforeClass;
// import org.junit.Test;
// import org.mockito.Matchers;
// import org.mockito.Mockito;
//
// import com.drwp.securityservice.authentication.AppConstants;
// import com.drwp.securityservice.authentication.BaseTest;
// import com.drwp.securityservice.authentication.BlackIceAuthenticationServiceMain;
// import com.drwp.securityservice.authentication.entity.AuthorizationCode;
// import com.drwp.securityservice.authentication.entity.AuthorizationCodeType;
// import com.drwp.securityservice.authentication.entity.LoginAudit;
// import com.drwp.securityservice.authentication.entity.RedirectResponse;
// import com.drwp.securityservice.authentication.entity.SmsCode;
// import com.drwp.securityservice.authentication.entity.TwoFactorAuthType;
// import com.drwp.securityservice.authentication.entity.User;
// import com.drwp.securityservice.authentication.entity.UserDirectory;
// import com.drwp.securityservice.authentication.exception.AuthenticationException;
// import com.drwp.securityservice.authentication.ldap.LDAPAuthenticationService;
// import com.drwp.securityservice.authentication.service.CookieService;
// import com.drwp.securityservice.authentication.service.MailService;
// import com.drwp.securityservice.authentication.utils.InputStreamToStringConverter;
// import com.drwp.securityservice.authentication.utils.UserDisplayEntity;
// import com.drwp.securityservice.authentication.utils.cookie.CookieCryptoUtils;
// import com.drwp.securityservice.authentication.utils.cookie.OunceCookie;
// import com.yubico.u2f.U2F;
// import com.yubico.u2f.exceptions.DeviceCompromisedException;
// import com.yubico.u2f.exceptions.NoEligibleDevicesException;
// import com.yubico.u2f.exceptions.U2fBadInputException;
//
// public class LoginControllerTest extends BaseTest {
//
// private WebClient client;
// private final static String ENDPOINT_ADDRESS = "http://localhost:9236";
// protected static Server server;
// private final static String USER_VERIFY_PATH = "/oauth2/user";
// private final static String CODE_PATH = "/oauth2/code";
// private final static String STATUS_PATH = "/oauth2/status";
// private final static String STATUS_RESPONSE = "<Status>UP</Status>";
// private static final String USERNAME = "gjoleka@spotify.com";
// private static final String DRWP_USERNAME = "drwpgjoleka@digitalriver.com";
// private static final String PASS = "NetGiroAdminPassword";
// private final static String EXPECTED_REDIRECT_URL = "http://localhost:9249/api/gateway/loginredirect?code=";
// public static final String X_XSRF_TOKEN = "X-XSRF-TOKEN";
// private static String cxrfCookie = null;
// private static MailService mailService = Mockito.mock(MailService.class);
// private static LDAPAuthenticationService ldapAuthnService = Mockito.mock(LDAPAuthenticationService.class);
// private CookieService cookieService;
// private CookieCryptoUtils cookieCryptoUtils;
//
// @BeforeClass
// public static void warmUp() throws Exception {
//
// Mockito.doNothing().when(mailService).sendUnlockAccountEmail(Matchers.any(User.class), Matchers.anyString());
// u2f = Mockito.mock(U2F.class);
// Mockito.when(ldapAuthnService.authenticateUser(Matchers.anyString(), Matchers.anyString())).thenReturn(true);
// server = BlackIceAuthenticationServiceMain.startAuthorizationCodeRestServices(9236, getSession(), cfg,
// mailService, ldapAuthnService, u2f, smsProviderClient);
//
// }
//
// @SuppressWarnings("unchecked")
// @Before
// public void setUp() throws U2fBadInputException, NoEligibleDevicesException, DeviceCompromisedException {
// cookieCryptoUtils = new CookieCryptoUtils("AbCdEfGhIjKlMnOp");
// cookieService = new CookieService(cookieCryptoUtils);
// cxrfCookie = cookieCryptoUtils.encrypt(cookieService.createCookieValueWithOnce(USERNAME));
// junitDao.saveApplication(createApplication());
// junitDao.storeCidrAddress("127.0.0.1/29", true);
// junitDao.storeCidrAddress("192.168.76.133/25", true);
//
// User user = createUser(USERNAME, Boolean.TRUE);
// user.setIsEmailVerified(Boolean.TRUE);
// junitDao.saveUser(user);
//
// initClient();
// Mockito.reset(u2f);
// Mockito.reset(smsProviderClient);
// Mockito.when(u2f.startAuthentication(Matchers.anyString(), Matchers.anyCollection()))
// .thenReturn(createAuthenticateRequestData());
// Mockito.when(u2f.finishAuthentication(Matchers.any(), Matchers.any(), Matchers.anyCollection()))
// .thenReturn(createDeviceRegistration());
// Mockito.doNothing().when(smsProviderClient).sendSms(Matchers.anyString());
// }
//
// private void initClient() {
// client = WebClient.create(ENDPOINT_ADDRESS);
// client.accept(MediaType.APPLICATION_JSON);
// client.type(MediaType.APPLICATION_FORM_URLENCODED);
// client.header("User-Agent",
// "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
// client.header(X_XSRF_TOKEN, cxrfCookie);
// }
//
// @AfterClass
// public static void destroy() {
// if (server != null) {
// server.stop();
// server.destroy();
// }
// }
//
// @Test
// public void testStaticContent_Headers() {
// client.path("/views/login/login.html");
// Response response = client.get();
// Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
// Assert.assertEquals("DENY", response.getHeaderString("X-Frame-Options"));
// Assert.assertEquals("1; mode=block", response.getHeaderString("X-XSS-Protection"));
// Assert.assertEquals("nosniff", response.getHeaderString("X-Content-Type-Options"));
// String expectCspValue = "default-src 'none'; script-src 'self' ; object-src 'none'; style-src 'self' 'unsafe-inline';
// img-src 'self'; media-src 'none'; "
// + "frame-src 'none'; font-src 'self'; connect-src 'self'";
// Assert.assertEquals(expectCspValue, response.getHeaderString("Content-Security-Policy"));
// Assert.assertEquals(expectCspValue, response.getHeaderString("X-Content-Security-Policy"));
// Assert.assertEquals(expectCspValue, response.getHeaderString("X-WebKit-CSP"));
// }
//
// @Test
// public void testIsAlive() {
// client.path(STATUS_PATH);
// Assert.assertEquals(STATUS_RESPONSE, client.get(String.class));
// }
//
// @Test
// public void verifyUser_OK() {
// client.path(USER_VERIFY_PATH);
// final Form form = new Form();
// form.set("username", USERNAME);
//
// final Response response = client.form(form);
// Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
// NewCookie cookie = response.getCookies().get(XSRF_COOKIE_NAME);
// OunceCookie ounceCookie = gson.fromJson(cookieCryptoUtils.decrypt(cookie.getValue()), OunceCookie.class);
// Assert.assertEquals(USERNAME, ounceCookie.getUserName());
// Assert.assertNotNull(ounceCookie.getDateMillis());
// UserDisplayEntity userDisplayEntity = getResponse(response, UserDisplayEntity.class);
// Assert.assertEquals(USERNAME, userDisplayEntity.getUsername());
// }
//
// @Test
// public void verifyUser_UserNotFound() {
// client.path(USER_VERIFY_PATH);
// final Form form = new Form();
// form.set("username", "whoami@digitalriver.com");
//
// final Response response = client.form(form);
// Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
// NewCookie cookie = response.getCookies().get(XSRF_COOKIE_NAME);
// OunceCookie ounceCookie = gson.fromJson(cookieCryptoUtils.decrypt(cookie.getValue()), OunceCookie.class);
// Assert.assertEquals("whoami@digitalriver.com", ounceCookie.getUserName());
// Assert.assertNotNull(ounceCookie.getDateMillis());
// UserDisplayEntity userDisplayEntity = getResponse(response, UserDisplayEntity.class);
// Assert.assertEquals("whoami@digitalriver.com", userDisplayEntity.getUsername());
// }
//
// @Test
// public void testAuthenticateSuccess() {
// client.path(CODE_PATH);
// final Form form = new Form();
// form.set("user", USERNAME);
// form.set("password", PASS);
// form.set("applicationId", CLIENT_APPLICATION_ID);
// form.set("applicationUri", CLIENT_APPLICATION_URL);
// client.header("referer", EXPECTED_REDIRECT_URL);
//
// final Response response = client.form(form);
// Assert.assertEquals(Status.SEE_OTHER.getStatusCode(), response.getStatus());
// final String responseString = response.getLocation().toString();
//
// final String authorizationCodeValue = responseString.split("code=")[1].split("&")[0];
// final AuthorizationCode authzCode = authorizationCodeDao
// .getValidAuthorizationCodeByAuthCodeAndAppClientIdAndAppClientSecret(authorizationCodeValue,
// CLIENT_APPLICATION_ID, CLIENT_APPLICATION_SECRET);
//
// Assert.assertTrue(responseString.contains(EXPECTED_REDIRECT_URL));
// Assert.assertNotNull(authzCode);
// Assert.assertEquals(authorizationCodeValue, authzCode.getAuthCode());
// Assert.assertEquals(CLIENT_APPLICATION_ID, authzCode.getClientApplicationId());
// Assert.assertEquals(CLIENT_APPLICATION_SECRET, authzCode.getClientSecret());
// Assert.assertEquals(Boolean.TRUE, authzCode.getIsValidCode());
// Assert.assertEquals(MASTER_MID, authzCode.getMasterMid());
// Assert.assertEquals(USERNAME, authzCode.getUserName());
// Assert.assertEquals("127.0.0.1", authzCode.getClientIpAddress());
// Assert.assertEquals("Chrome-50.0.2661.102", authzCode.getClientBrowserInfo());
// Assert.assertEquals("Windows 7", authzCode.getClientOperatingSystem());
// Assert.assertEquals("Computer", authzCode.getClientDeviceType());
//
// LoginAudit loginAudit = junitDao.getLoginAuditByMM(MASTER_MID, USERNAME).get(0);
// Assert.assertEquals("OK", loginAudit.getMessage());
// Assert.assertEquals("SUCCESS", loginAudit.getStatus());
// Assert.assertEquals(EXPECTED_REDIRECT_URL, loginAudit.getReferer());
// Assert.assertEquals("127.0.0.1", loginAudit.getIpAddress());
// Assert.assertEquals("Chrome-50.0.2661.102", loginAudit.getClientBrowserInfo());
// Assert.assertEquals("Windows 7", loginAudit.getClientOperatingSystem());
// Assert.assertEquals("Computer", loginAudit.getClientDeviceType());
// }
//
// @Test
// public void testAuthenticateSuccess_WithRedirect_False() {
// client.path(CODE_PATH);
// final Form form = new Form();
// form.set("user", USERNAME);
// form.set("password", PASS);
// form.set("applicationId", CLIENT_APPLICATION_ID);
// form.set("applicationUri", CLIENT_APPLICATION_URL);
// form.set("rd", "false");
// client.header("referer", EXPECTED_REDIRECT_URL);
// final Response response = client.form(form);
// Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
// Assert.assertEquals("no-cache, no-store, must-revalidate", response.getHeaderString(HttpHeaders.CACHE_CONTROL));
// Assert.assertEquals("no-cache", response.getHeaderString("Pragma"));
// Assert.assertEquals("DENY", response.getHeaderString("X-Frame-Options"));
// Assert.assertEquals("1; mode=block", response.getHeaderString("X-XSS-Protection"));
// Assert.assertEquals("nosniff", response.getHeaderString("X-Content-Type-Options"));
// final String responseString = getResponse(response, RedirectResponse.class).getRedirectUrl();
//
// final String authorizationCodeValue = responseString.split("code=")[1].split("&")[0];
// final AuthorizationCode authzCode = authorizationCodeDao
// .getValidAuthorizationCodeByAuthCodeAndAppClientIdAndAppClientSecret(authorizationCodeValue,
// CLIENT_APPLICATION_ID, CLIENT_APPLICATION_SECRET);
//
// Assert.assertTrue(responseString.contains(EXPECTED_REDIRECT_URL));
// Assert.assertNotNull(authzCode);
// Assert.assertEquals(authorizationCodeValue, authzCode.getAuthCode());
// Assert.assertEquals(CLIENT_APPLICATION_ID, authzCode.getClientApplicationId());
// Assert.assertEquals(CLIENT_APPLICATION_SECRET, authzCode.getClientSecret());
// Assert.assertEquals(Boolean.TRUE, authzCode.getIsValidCode());
// Assert.assertEquals(MASTER_MID, authzCode.getMasterMid());
// Assert.assertEquals(USERNAME, authzCode.getUserName());
// Assert.assertEquals("127.0.0.1", authzCode.getClientIpAddress());
// Assert.assertEquals("Chrome-50.0.2661.102", authzCode.getClientBrowserInfo());
// Assert.assertEquals("Windows 7", authzCode.getClientOperatingSystem());
// Assert.assertEquals("Computer", authzCode.getClientDeviceType());
//
// LoginAudit loginAudit = junitDao.getLoginAuditByMM(MASTER_MID, USERNAME).get(0);
// Assert.assertEquals("OK", loginAudit.getMessage());
// Assert.assertEquals("SUCCESS", loginAudit.getStatus());
// Assert.assertEquals(EXPECTED_REDIRECT_URL, loginAudit.getReferer());
// Assert.assertEquals("127.0.0.1", loginAudit.getIpAddress());
// Assert.assertEquals("Chrome-50.0.2661.102", loginAudit.getClientBrowserInfo());
// Assert.assertEquals("Windows 7", loginAudit.getClientOperatingSystem());
// Assert.assertEquals("Computer", loginAudit.getClientDeviceType());
//
// validateCookieRemoval(response);
// }
//
// @Test
// public void testAuthenticateWrongPassFailure() {
// client.path(CODE_PATH);
// final Form form = new Form();
// form.set("user", USERNAME);
// form.set("password", "sdfafafa");
// form.set("applicationId", CLIENT_APPLICATION_ID);
// form.set("applicationUri", CLIENT_APPLICATION_URL);
//
// final String fullUrl =
// "http://pcc.drwp.com:8080?error=%7B%22error%22%3A%22invalid_request%22%2C%22error_description%22%3A%22Invalid+username+or+password%22%7D";
// client.header("referer", fullUrl);
// final Response response = client.form(form);
// assertRedirectRequest(response, "Invalid username or password");
// }
//
// @Test
// public void testAuthenticate_UnknownUser_CheckAudit() {
// final Form form = new Form();
// form.set("user", "UNKNOWN@digitalriver.com");
// form.set("password", "UNKNOWN");
// form.set("applicationId", CLIENT_APPLICATION_ID);
// form.set("applicationUri", CLIENT_APPLICATION_URL);
// final String fullUrl =
// "http://pcc.drwp.com:8080?error=%7B%22error%22%3A%22invalid_request%22%2C%22error_description%22%3A%22Invalid+username+or+password%22%7D";
// client.reset();
// client.header("User-Agent",
// "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
// client.path(CODE_PATH);
// client.header("referer", fullUrl);
// cxrfCookie = cookieCryptoUtils.encrypt(cookieService.createCookieValueWithOnce("UNKNOWN@digitalriver.com"));
// client.header(X_XSRF_TOKEN, cxrfCookie);
// final Response response = client.form(form);
// assertRedirectRequest(response, "Invalid username or password");
// Assert.assertTrue(junitDao.getLoginAudits().isEmpty());
// }
//
// @Test
// public void testAuthenticateExistingUsersWithNullPassExpSuccess() {
// User user = createUser(USERNAME, true);
// user.setPasswordExpirationTimestamp(null);
// user.setIsEmailVerified(Boolean.TRUE);
// junitDao.saveUser(user);
// Assert.assertNull(user.getPasswordExpirationTimestamp());
// client.path(CODE_PATH);
// final Form form = new Form();
// form.set("user", USERNAME);
// form.set("password", PASS);
// form.set("applicationId", CLIENT_APPLICATION_ID);
// form.set("applicationUri", CLIENT_APPLICATION_URL);
// client.header("referer", EXPECTED_REDIRECT_URL);
// final Response response = client.form(form);
// Assert.assertEquals(Status.SEE_OTHER.getStatusCode(), response.getStatus());
// final String responseString = response.getLocation().toString();
//
// final String authorizationCodeValue = responseString.split("code=")[1].split("&")[0];
// final AuthorizationCode authzCode = authorizationCodeDao
// .getValidAuthorizationCodeByAuthCodeAndAppClientIdAndAppClientSecret(authorizationCodeValue,
// CLIENT_APPLICATION_ID, CLIENT_APPLICATION_SECRET);
// Assert.assertTrue(responseString.contains(EXPECTED_REDIRECT_URL));
// Assert.assertNotNull(authzCode);
// Assert.assertEquals(authorizationCodeValue, authzCode.getAuthCode());
// Assert.assertEquals(CLIENT_APPLICATION_ID, authzCode.getClientApplicationId());
// Assert.assertEquals(CLIENT_APPLICATION_SECRET, authzCode.getClientSecret());
// Assert.assertEquals(Boolean.TRUE, authzCode.getIsValidCode());
// Assert.assertEquals(MASTER_MID, authzCode.getMasterMid());
// Assert.assertEquals(USERNAME, authzCode.getUserName());
//
// }
//
// @Test
// public void testAuthenticatePassExpired() {
// User user = createUser(USERNAME, true);
// user.setPasswordExpirationTimestamp(new Timestamp(new DateTime().minusMinutes(1).getMillis()));
// junitDao.saveUser(user);
// client.path(CODE_PATH);
// final Form form = new Form();
// form.set("user", USERNAME);
// form.set("password", PASS);
// form.set("applicationId", CLIENT_APPLICATION_ID);
// form.set("applicationUri", CLIENT_APPLICATION_URL);
//
// final String fullUrl =
// "http://pcc.drwp.com:8080?error=%7B%22error%22%3A%22invalid_request%22%2C%22error_description%22%3A%22Invalid+username+or+password%22%7D";
// client.header("referer", fullUrl);
// final Response response = client.form(form);
// assertRedirectRequest(response, "Invalid username or password");
// }
//
// @Test
// public void testAuthenticateWrongPassFailureLockAccount() throws InterruptedException {
// client.path(CODE_PATH);
// final Form form = new Form();
// form.set("user", USERNAME);
// form.set("password", "sdfafafa");
// form.set("applicationId", CLIENT_APPLICATION_ID);
// form.set("applicationUri", CLIENT_APPLICATION_URL);
//
// final String fullUrl =
// "http://pcc.drwp.com:8080?error=%7B%22error%22%3A%22invalid_request%22%2C%22error_description%22%3A%22Invalid+username+or+password%22%7D";
// client.header("referer", fullUrl);
// final Response response = client.form(form);
// assertRedirectRequest(response, "Invalid username or password");
//
// final Response response2 = client.form(form);
// assertRedirectRequest(response2, "Invalid username or password");
//
// final Response response3 = client.form(form);
// assertRedirectRequest(response3, "Invalid username or password");
//
// final Response response4 = client.form(form);
// assertRedirectRequest(response4, "Invalid username or password");
// Assert.assertEquals(2, junitDao.getLoginAuditByMM(MASTER_MID, USERNAME).size());
//
// Thread.sleep(4000);
//
// final Form formSuccess = new Form();
// formSuccess.set("user", USERNAME);
// formSuccess.set("password", PASS);
// formSuccess.set("applicationId", CLIENT_APPLICATION_ID);
// formSuccess.set("applicationUri", CLIENT_APPLICATION_URL);
// client.header("referer", EXPECTED_REDIRECT_URL);
// final Response responseSuccess = client.form(formSuccess);
// Assert.assertEquals(Status.SEE_OTHER.getStatusCode(), response.getStatus());
// final String responseString = responseSuccess.getLocation().toString();
//
// final String authorizationCodeValue = responseString.split("code=")[1].split("&")[0];
// final AuthorizationCode authzCode = authorizationCodeDao
// .getValidAuthorizationCodeByAuthCodeAndAppClientIdAndAppClientSecret(authorizationCodeValue,
// CLIENT_APPLICATION_ID, CLIENT_APPLICATION_SECRET);
//
// Assert.assertTrue(responseString.contains(EXPECTED_REDIRECT_URL));
// Assert.assertNotNull(authzCode);
// Assert.assertEquals(authorizationCodeValue, authzCode.getAuthCode());
// Assert.assertEquals(CLIENT_APPLICATION_ID, authzCode.getClientApplicationId());
// Assert.assertEquals(CLIENT_APPLICATION_SECRET, authzCode.getClientSecret());
// Assert.assertEquals(Boolean.TRUE, authzCode.getIsValidCode());
// Assert.assertEquals(MASTER_MID, authzCode.getMasterMid());
// Assert.assertEquals(USERNAME, authzCode.getUserName());
//
// }
//
// @Test
// public void testAuthenticateWrongUserNameFailure() {
// client.reset();
// client.path(CODE_PATH);
// cxrfCookie = cookieCryptoUtils.encrypt(cookieService.createCookieValueWithOnce("USERNAME@test.com"));
// client.header("User-Agent",
// "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
// client.header(X_XSRF_TOKEN, cxrfCookie);
// final Form form = new Form();
// form.set("user", "USERNAME@test.com");
// form.set("password", PASS);
// form.set("applicationId", CLIENT_APPLICATION_ID);
// form.set("applicationUri", CLIENT_APPLICATION_URL);
// final String fullUrl =
// "http://pcc.drwp.com:8080?error=%7B%22error%22%3A%22invalid_request%22%2C%22error_description%22%3A%22Invalid+username+or+password%22%7D";
// client.header("referer", fullUrl);
// final Response response = client.form(form);
// assertRedirectRequest(response, "Invalid username or password");
// }
//
// @Test
// public void testAuthenticateWrongClientAppIdFailure() {
// client.path(CODE_PATH);
// final Form form = new Form();
// form.set("user", USERNAME);
// form.set("password", PASS);
// form.set("applicationId", "CLIENT_APPLICATION_ID");
// form.set("applicationUri", CLIENT_APPLICATION_URL);
//
// final String fullUrl =
// "http://pcc.drwp.com:8080&error=%7B%22error%22%3A%22invalid_request%22%2C%22error_description%22%3A%22Not+a+valid+clientApplicationId%3A+pcc%2C+or+client+applicationUrl%3A+CLIENT_APPLICATION_URL%22%7D";
// client.header("referer", fullUrl);
// final Response response = client.form(form);
// Assert.assertEquals(303, response.getStatus());
// Assert.assertEquals(
// "http://pcc.drwp.com:8080&error=%7B%22error%22%3A%22invalid_request%22%2C%22error_description%22%3A%22Not+a+valid+Client+Application+ID%3A+CLIENT_APPLICATION_ID%22%7D",
// response.getLocation().toString());
// }
//
// @Test
// public void testAuthenticateWrongClientAppURLFailure() {
// client.path(CODE_PATH);
// final Form form = new Form();
// form.set("user", USERNAME);
// form.set("password", PASS);
// form.set("applicationId", CLIENT_APPLICATION_ID);
// form.set("applicationUri", "CLIENT_APPLICATION_URL");
//
// final String fullUrl =
// "http://pcc.drwp.com:8080&error=%7B%22error%22%3A%22invalid_request%22%2C%22error_description%22%3A%22Not+a+valid+clientApplicationId%3A+pcc%2C+or+client+applicationUrl%3A+CLIENT_APPLICATION_URL%22%7D";
// client.header("referer", fullUrl);
// final Response response = client.form(form);
//
// Assert.assertEquals(303, response.getStatus());
// Assert.assertEquals(
// "http://pcc.drwp.com:8080&error=%7B%22error%22%3A%22invalid_request%22%2C%22error_description%22%3A%22Not+valid+redirect+URL%3A+CLIENT_APPLICATION_URL%22%7D",
// response.getLocation().toString());
// }
//
// @Test
// public void testAuthenticateWrongClientAppFullURLFailure() {
// client.path(CODE_PATH);
// final Form form = new Form();
// form.set("user", USERNAME);
// form.set("password", PASS);
// form.set("applicationId", CLIENT_APPLICATION_ID);
// form.set("applicationUri", "CLIENT_APPLICATION_URL");
// final String fullUrl =
// "http://pcc.drwp.com:8080&error=%7B%22error%22%3A%22invalid_request%22%2C%22error_description%22%3A%22Not+a+valid+clientApplicationId%3A+pcc%2C+or+client+applicationUrl%3A+CLIENT_APPLICATION_URL%22%7D";
// client.header("referer", fullUrl);
// final Response response = client.form(form);
// Assert.assertEquals(303, response.getStatus());
// Assert.assertEquals(
// "http://pcc.drwp.com:8080&error=%7B%22error%22%3A%22invalid_request%22%2C%22error_description%22%3A%22Not+valid+redirect+URL%3A+CLIENT_APPLICATION_URL%22%7D",
// response.getLocation().toString());
// }
//
// @Test
// public void testAuthenticateEmptyUserFailure() {
// client.reset();
// client.path(CODE_PATH);
// client.header("User-Agent",
// "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
// cxrfCookie = cookieCryptoUtils.encrypt(cookieService.createCookieValueWithOnce(USER_NAME));
// client.header(X_XSRF_TOKEN, cxrfCookie);
// final Form form = new Form();
// form.set("user", "");
// form.set("password", PASS);
// form.set("applicationId", CLIENT_APPLICATION_ID);
// form.set("applicationUri", CLIENT_APPLICATION_URL);
//
// final Response response = client.form(form);
//
// Assert.assertNotNull(response);
// Assert.assertEquals("{\"error\":\"invalid_request\",\"error_description\":\"user cannot be null or empty\"}",
// InputStreamToStringConverter.convertStreamToString((InputStream) response.getEntity()));
// Assert.assertEquals(400, response.getStatus());
//
// }
//
// @Test
// public void testAuthenticateEmptyPassFailure() {
// client.path(CODE_PATH);
// final Form form = new Form();
// form.set("user", USERNAME);
// form.set("password", "");
// form.set("applicationId", CLIENT_APPLICATION_ID);
// form.set("applicationUri", CLIENT_APPLICATION_URL);
//
// final Response response = client.form(form);
//
// Assert.assertNotNull(response);
// Assert.assertEquals(
// "{\"error\":\"invalid_request\",\"error_description\":\"password cannot be null or empty\"}",
// InputStreamToStringConverter.convertStreamToString((InputStream) response.getEntity()));
// Assert.assertEquals(400, response.getStatus());
//
// }
//
// @Test
// public void testAuthenticateEmptyClientAppIdFailure() {
// client.path(CODE_PATH);
// final Form form = new Form();
// form.set("user", USERNAME);
// form.set("password", PASS);
// form.set("applicationId", "");
// form.set("applicationUri", CLIENT_APPLICATION_URL);
//
// final Response response = client.form(form);
//
// Assert.assertNotNull(response);
// Assert.assertEquals(
// "{\"error\":\"invalid_request\",\"error_description\":\"applicationId cannot be null or empty\"}",
// InputStreamToStringConverter.convertStreamToString((InputStream) response.getEntity()));
// Assert.assertEquals(400, response.getStatus());
//
// }
//
// @Test
// public void testAuthenticateEmptyClientAppUrlFailure() {
// client.path(CODE_PATH);
// final Form form = new Form();
// form.set("user", USERNAME);
// form.set("password", PASS);
// form.set("applicationId", CLIENT_APPLICATION_ID);
// form.set("applicationUri", "");
//
// final Response response = client.form(form);
//
// Assert.assertNotNull(response);
// Assert.assertEquals(
// "{\"error\":\"invalid_request\",\"error_description\":\"applicationUri cannot be null or empty\"}",
// InputStreamToStringConverter.convertStreamToString((InputStream) response.getEntity()));
// Assert.assertEquals(400, response.getStatus());
// }
//
// @Test
// public void testAuthenticateSuccess_DRWPUser_WithLDAP() {
// client.reset();
// client.header("User-Agent",
// "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
//
// User user = createUser(DRWP_USERNAME, true);
// user.setMasterMid(AppConstants.DRWP_MASTER_MID);
// user.setUserDirectory(UserDirectory.LDAP.name());
// user.setIsEmailVerified(Boolean.TRUE);
// junitDao.saveUser(user);
// client.path(CODE_PATH);
// client.header(X_XSRF_TOKEN, cookieCryptoUtils.encrypt(cookieService.createCookieValueWithOnce(DRWP_USERNAME)));
// final Form form = new Form();
// form.set("user", DRWP_USERNAME);
// form.set("password", PASS);
// form.set("applicationId", CLIENT_APPLICATION_ID);
// form.set("applicationUri", CLIENT_APPLICATION_URL);
// client.header("referer", EXPECTED_REDIRECT_URL);
// final Response response = client.form(form);
// Assert.assertEquals(Status.SEE_OTHER.getStatusCode(), response.getStatus());
// final String responseString = response.getLocation().toString();
//
// final String authorizationCodeValue = responseString.split("code=")[1].split("&")[0];
// final AuthorizationCode authzCode = authorizationCodeDao
// .getValidAuthorizationCodeByAuthCodeAndAppClientIdAndAppClientSecret(authorizationCodeValue,
// CLIENT_APPLICATION_ID, CLIENT_APPLICATION_SECRET);
// Mockito.verify(ldapAuthnService, Mockito.times(1)).authenticateUser(Matchers.anyString(), Matchers.anyString());
// Assert.assertTrue(responseString.contains(EXPECTED_REDIRECT_URL));
// Assert.assertNotNull(authzCode);
// Assert.assertEquals(authorizationCodeValue, authzCode.getAuthCode());
// Assert.assertEquals(CLIENT_APPLICATION_ID, authzCode.getClientApplicationId());
// Assert.assertEquals(CLIENT_APPLICATION_SECRET, authzCode.getClientSecret());
// Assert.assertEquals(Boolean.TRUE, authzCode.getIsValidCode());
// Assert.assertEquals(AppConstants.DRWP_MASTER_MID, authzCode.getMasterMid());
// Assert.assertEquals(DRWP_USERNAME, authzCode.getUserName());
// Assert.assertEquals("127.0.0.1", authzCode.getClientIpAddress());
// Assert.assertEquals("Chrome-50.0.2661.102", authzCode.getClientBrowserInfo());
// Assert.assertEquals("Windows 7", authzCode.getClientOperatingSystem());
// Assert.assertEquals("Computer", authzCode.getClientDeviceType());
//
// LoginAudit loginAudit = junitDao.getLoginAuditByMM(AppConstants.DRWP_MASTER_MID, DRWP_USERNAME).get(0);
// Assert.assertEquals("OK", loginAudit.getMessage());
// Assert.assertEquals("SUCCESS", loginAudit.getStatus());
// Assert.assertEquals(EXPECTED_REDIRECT_URL, loginAudit.getReferer());
// Assert.assertEquals("127.0.0.1", loginAudit.getIpAddress());
// Assert.assertEquals("Chrome-50.0.2661.102", loginAudit.getClientBrowserInfo());
// Assert.assertEquals("Windows 7", loginAudit.getClientOperatingSystem());
// Assert.assertEquals("Computer", loginAudit.getClientDeviceType());
// }
//
// @Test
// public void testAuthenticateSuccess_WithU2FAuthentication() {
// User user = createUser(USERNAME, Boolean.TRUE);
// user.setIsTwoFactorAuthenticationEnabled(true);
// user.setTwoFactorAuthenticationType("U2F");
// user.setIsEmailVerified(Boolean.TRUE);
// junitDao.saveUser(user);
// client.path(CODE_PATH);
// final Form form = new Form();
// form.set("user", USERNAME);
// form.set("password", PASS);
// form.set("applicationId", CLIENT_APPLICATION_ID);
// form.set("applicationUri", CLIENT_APPLICATION_URL);
// client.header("referer", EXPECTED_REDIRECT_URL);
// final Response response = client.form(form);
// Assert.assertEquals(Status.SEE_OTHER.getStatusCode(), response.getStatus());
// final String responseString = response.getLocation().toString();
// final String authorizationCodeValue = responseString.split("ac=")[1].split("&")[0];
// Assert.assertEquals(
// "http://authn.drwp.com/#/u2f/authentications?ac=" + authorizationCodeValue
// +
// "&applicationId=PCC&applicationUri=http%3A%2F%2Flocalhost%3A9249%2Fapi%2Fgateway%2Floginredirect%3Fto%3D%2Fcdn%2Fdashboard%2Findex.html%23%2Fdashboard.html",
// responseString);
// final AuthorizationCode authzCode = authorizationCodeDao
// .getValidAuthorizationCodeByAuthCodeAndAppClientIdAndAppClientSecret(authorizationCodeValue,
// CLIENT_APPLICATION_ID, CLIENT_APPLICATION_SECRET);
// NewCookie cookie = response.getCookies().get(XSRF_COOKIE_NAME);
// OunceCookie ounceCookie = gson.fromJson(cookieCryptoUtils.decrypt(cookie.getValue()), OunceCookie.class);
// Assert.assertEquals(USERNAME, ounceCookie.getUserName());
// Assert.assertNotNull(ounceCookie.getDateMillis());
// Assert.assertNotNull(authzCode);
// Assert.assertEquals(authorizationCodeValue, authzCode.getAuthCode());
// Assert.assertEquals(CLIENT_APPLICATION_ID, authzCode.getClientApplicationId());
// Assert.assertEquals(CLIENT_APPLICATION_SECRET, authzCode.getClientSecret());
// Assert.assertEquals(Boolean.TRUE, authzCode.getIsValidCode());
// Assert.assertEquals(MASTER_MID, authzCode.getMasterMid());
// Assert.assertEquals(USERNAME, authzCode.getUserName());
// Assert.assertEquals(AuthorizationCodeType.U2F_CODE, authzCode.getAuthzCodeType());
// Assert.assertEquals("127.0.0.1", authzCode.getClientIpAddress());
// Assert.assertEquals("Chrome-50.0.2661.102", authzCode.getClientBrowserInfo());
// Assert.assertEquals("Windows 7", authzCode.getClientOperatingSystem());
// Assert.assertEquals("Computer", authzCode.getClientDeviceType());
//
// // Start Authentication
// initClient();
// client.header("referer", EXPECTED_REDIRECT_URL);
// junitDao.storeDevice(MASTER_MID, USERNAME, true);
// Response response2 = client.path("/v1/u2f/authentications").query("username", USERNAME)
// .query("applicationId", CLIENT_APPLICATION_ID).query("applicationUrl", CLIENT_APPLICATION_URL)
// .query("ac", authorizationCodeValue).get();
// Assert.assertEquals(Status.OK.getStatusCode(), response2.getStatus());
// String requestData = u2fAuthenticationDao.getRequestData(USERNAME, U2F_CHALLENGE);
// Assert.assertEquals(createAuthenticateRequestData().toJson(), requestData);
// String stringResponse = getStringResponse(response2);
// Assert.assertTrue(stringResponse.contains(requestData));
// // Finish Authentication
// initClient();
// client.header("referer", EXPECTED_REDIRECT_URL);
// junitDao.storeDeviceCounter(USERNAME);
// final Form form2 = new Form();
// form2.set("tokenResponse", getTokenResponse());
// form2.set("username", USERNAME);
// form2.set("applicationId", CLIENT_APPLICATION_ID);
// form2.set("applicationUri", CLIENT_APPLICATION_URL);
// form2.set("ac", authorizationCodeValue);
// Response response3 = client.path("/v1/u2f/authentications").post(form2);
// Assert.assertEquals(Status.OK.getStatusCode(), response3.getStatus());
// final String redirectLoc = getResponse(response3, RedirectResponse.class).getRedirectUrl();
// final String loginAuthzCodeValue = redirectLoc.split("code=")[1].split("&")[0];
// Assert.assertEquals("http://localhost:9249/api/gateway/loginredirect?code=" + loginAuthzCodeValue +
// "&to=/cdn/dashboard/index.html#/dashboard.html", redirectLoc);
// final AuthorizationCode loginAuthzCode = authorizationCodeDao
// .getValidAuthorizationCodeByAuthCodeAndAppClientIdAndAppClientSecret(loginAuthzCodeValue,
// CLIENT_APPLICATION_ID, CLIENT_APPLICATION_SECRET);
// Assert.assertEquals(AuthorizationCodeType.LOGIN, loginAuthzCode.getAuthzCodeType());
//
// // Validate Login Audit
// LoginAudit loginAudit = junitDao.getLoginAuditByMM(MASTER_MID, USERNAME).get(0);
// Assert.assertEquals("OK", loginAudit.getMessage());
// Assert.assertEquals("SUCCESS", loginAudit.getStatus());
// Assert.assertEquals(EXPECTED_REDIRECT_URL, loginAudit.getReferer());
// Assert.assertEquals("127.0.0.1", loginAudit.getIpAddress());
// Assert.assertEquals("Chrome-50.0.2661.102", loginAudit.getClientBrowserInfo());
// Assert.assertEquals("Windows 7", loginAudit.getClientOperatingSystem());
// Assert.assertEquals("Computer", loginAudit.getClientDeviceType());
// validateCookieRemoval(response3);
// }
//
// @Test
// public void testAuthenticateSuccess_InactiveRegisteredDevicesFound() {
// User user = createUser(USERNAME, Boolean.TRUE);
// user.setIsTwoFactorAuthenticationEnabled(true);
// user.setTwoFactorAuthenticationType("U2F");
// user.setIsEmailVerified(Boolean.TRUE);
// junitDao.saveUser(user);
// client.path(CODE_PATH);
// final Form form = new Form();
// form.set("user", USERNAME);
// form.set("password", PASS);
// form.set("applicationId", CLIENT_APPLICATION_ID);
// form.set("applicationUri", CLIENT_APPLICATION_URL);
// client.header("referer", EXPECTED_REDIRECT_URL);
// final Response response = client.form(form);
// Assert.assertEquals(Status.SEE_OTHER.getStatusCode(), response.getStatus());
// final String responseString = response.getLocation().toString();
// final String authorizationCodeValue = responseString.split("ac=")[1].split("&")[0];
// Assert.assertEquals(
// "http://authn.drwp.com/#/u2f/authentications?ac=" + authorizationCodeValue
// +
// "&applicationId=PCC&applicationUri=http%3A%2F%2Flocalhost%3A9249%2Fapi%2Fgateway%2Floginredirect%3Fto%3D%2Fcdn%2Fdashboard%2Findex.html%23%2Fdashboard.html",
// responseString);
// final AuthorizationCode authzCode = authorizationCodeDao
// .getValidAuthorizationCodeByAuthCodeAndAppClientIdAndAppClientSecret(authorizationCodeValue,
// CLIENT_APPLICATION_ID, CLIENT_APPLICATION_SECRET);
//
// Assert.assertNotNull(authzCode);
// Assert.assertEquals(authorizationCodeValue, authzCode.getAuthCode());
// Assert.assertEquals(CLIENT_APPLICATION_ID, authzCode.getClientApplicationId());
// Assert.assertEquals(CLIENT_APPLICATION_SECRET, authzCode.getClientSecret());
// Assert.assertEquals(Boolean.TRUE, authzCode.getIsValidCode());
// Assert.assertEquals(MASTER_MID, authzCode.getMasterMid());
// Assert.assertEquals(USERNAME, authzCode.getUserName());
// Assert.assertEquals(AuthorizationCodeType.U2F_CODE, authzCode.getAuthzCodeType());
// Assert.assertEquals("127.0.0.1", authzCode.getClientIpAddress());
// Assert.assertEquals("Chrome-50.0.2661.102", authzCode.getClientBrowserInfo());
// Assert.assertEquals("Windows 7", authzCode.getClientOperatingSystem());
// Assert.assertEquals("Computer", authzCode.getClientDeviceType());
//
// // Start Authentication
// initClient();
// junitDao.storeDevice(MASTER_MID, USERNAME, false);
// client.header("referer", EXPECTED_REDIRECT_URL);
// Response response2 = client.path("/v1/u2f/authentications").query("username", USERNAME)
// .query("applicationId", CLIENT_APPLICATION_ID).query("applicationUrl", CLIENT_APPLICATION_URL)
// .query("ac", authorizationCodeValue).get();
// assertBadRequest(response2, "No registered devices found");
// try {
// u2fAuthenticationDao.getRequestData(USERNAME, U2F_CHALLENGE);
// Assert.fail("should have failed");
// } catch (AuthenticationException ex) {
// assertBadRequestExceptionResponse(ex.getResponse(), "No request data found for device, please try again");
// }
// }
//
// @Test
// public void testAuthenticateSuccess_With2FSMSAuthentication() {
// User user = createUser(USERNAME, Boolean.TRUE);
// user.setIsTwoFactorAuthenticationEnabled(true);
// user.setTwoFactorAuthenticationType(TwoFactorAuthType.SMS.name());
// user.setIsEmailVerified(Boolean.TRUE);
// junitDao.saveUser(user);
// client.path(CODE_PATH);
// final Form form = new Form();
// form.set("user", USERNAME);
// form.set("password", PASS);
// form.set("applicationId", CLIENT_APPLICATION_ID);
// form.set("applicationUri", CLIENT_APPLICATION_URL);
// form.set("rd", "false");
// client.header("referer", EXPECTED_REDIRECT_URL);
// final Response response = client.form(form);
// Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
// final String responseString = getResponse(response, RedirectResponse.class).getRedirectUrl();
// final String smsLinkAuthzCodeValue = responseString.split("&")[0].split("=")[1];
// Assert.assertEquals(
// "http://authn.drwp.com/#/sms/authentications?authzCode=" + smsLinkAuthzCodeValue
// +
// "&applicationId=PCC&applicationUri=http%3A%2F%2Flocalhost%3A9249%2Fapi%2Fgateway%2Floginredirect%3Fto%3D%2Fcdn%2Fdashboard%2Findex.html%23%2Fdashboard.html",
// responseString);
//
// NewCookie cookie = response.getCookies().get(XSRF_COOKIE_NAME);
// OunceCookie ounceCookie = gson.fromJson(cookieCryptoUtils.decrypt(cookie.getValue()), OunceCookie.class);
// Assert.assertEquals(USERNAME, ounceCookie.getUserName());
// Assert.assertNotNull(ounceCookie.getDateMillis());
// final AuthorizationCode smsLinkAuthCode = authorizationCodeDao.getRecentAuthorizationCode(USERNAME,
// AuthorizationCodeType.SMS_2FLINK_CODE);
// Assert.assertEquals(smsLinkAuthzCodeValue, smsLinkAuthCode.getAuthCode());
// Assert.assertEquals(CLIENT_APPLICATION_ID, smsLinkAuthCode.getClientApplicationId());
// Assert.assertEquals(CLIENT_APPLICATION_SECRET, smsLinkAuthCode.getClientSecret());
// Assert.assertEquals(Boolean.TRUE, smsLinkAuthCode.getIsValidCode());
// Assert.assertEquals(MASTER_MID, smsLinkAuthCode.getMasterMid());
// Assert.assertEquals(USERNAME, smsLinkAuthCode.getUserName());
// Assert.assertEquals(AuthorizationCodeType.SMS_2FLINK_CODE, smsLinkAuthCode.getAuthzCodeType());
// Assert.assertEquals("127.0.0.1", smsLinkAuthCode.getClientIpAddress());
// Assert.assertEquals("Chrome-50.0.2661.102", smsLinkAuthCode.getClientBrowserInfo());
// Assert.assertEquals("Windows 7", smsLinkAuthCode.getClientOperatingSystem());
// Assert.assertEquals("Computer", smsLinkAuthCode.getClientDeviceType());
//
// SmsCode smsCode = smsCodeDao.getRecentSmsCode(USERNAME, CLIENT_APPLICATION_ID);
// Assert.assertEquals(CLIENT_APPLICATION_ID, smsCode.getClientApplicationId());
// Assert.assertEquals(6, smsCode.getCode().length());
// Assert.assertTrue(new Timestamp(System.currentTimeMillis()).before(smsCode.getCodeExpirationTimestamp()));
// Assert.assertTrue(new Timestamp(System.currentTimeMillis()).after(smsCode.getCodeSentTimestamp()));
// Assert.assertNotNull(smsCode.getCodeTs());
// Assert.assertTrue(new Timestamp(System.currentTimeMillis()).after(smsCode.getCreatedTimestamp()));
// Assert.assertTrue(smsCode.getIsValid());
// Assert.assertEquals(USERNAME, smsCode.getUserName());
// final Form formSmsCode = new Form();
// formSmsCode.set("authzCode", smsLinkAuthzCodeValue);
// formSmsCode.set("code", smsCode.getCode());
// formSmsCode.set("applicationId", CLIENT_APPLICATION_ID);
// formSmsCode.set("applicationUri", CLIENT_APPLICATION_URL);
//
// initClient();
// client.path("/v1/sms/authentications/code");
// client.header("referer", EXPECTED_REDIRECT_URL);
// final Response responseSmsCode = client.form(formSmsCode);
//
// Assert.assertEquals(Status.OK.getStatusCode(), responseSmsCode.getStatus());
//
// final String smsResponseRedirect = getResponse(responseSmsCode, RedirectResponse.class).getRedirectUrl();
//
// Assert.assertTrue(smsResponseRedirect,
// smsResponseRedirect.contains("http://localhost:9249/api/gateway/loginredirect?code="));
// final String authorizationCodeValue = smsResponseRedirect.split("code=")[1].split("&")[0];
// final AuthorizationCode authzCode = authorizationCodeDao.getRecentAuthorizationCode(USERNAME,
// AuthorizationCodeType.LOGIN);
// Assert.assertEquals(authorizationCodeValue, authzCode.getAuthCode());
// // Validate Login Audit
// LoginAudit loginAudit = junitDao.getLoginAuditByMM(MASTER_MID, USERNAME).get(0);
// Assert.assertEquals("OK", loginAudit.getMessage());
// Assert.assertEquals("SUCCESS", loginAudit.getStatus());
// Assert.assertEquals(EXPECTED_REDIRECT_URL, loginAudit.getReferer());
// Assert.assertEquals("127.0.0.1", loginAudit.getIpAddress());
// Assert.assertEquals("Chrome-50.0.2661.102", loginAudit.getClientBrowserInfo());
// Assert.assertEquals("Windows 7", loginAudit.getClientOperatingSystem());
// Assert.assertEquals("Computer", loginAudit.getClientDeviceType());
//
// Assert.assertNull(smsCodeDao.getRecentSmsCode(USERNAME, CLIENT_APPLICATION_ID));
// validateCookieRemoval(responseSmsCode);
// }
//
// @Test
// public void testAuthenticateSuccess_With2FSMS_LoginTwice_SameSmsCode() {
// User user = createUser(USERNAME, Boolean.TRUE);
// user.setIsTwoFactorAuthenticationEnabled(true);
// user.setTwoFactorAuthenticationType(TwoFactorAuthType.SMS.name());
// junitDao.saveUser(user);
// client.path(CODE_PATH);
// final Form form = new Form();
// form.set("user", USERNAME);
// form.set("password", PASS);
// form.set("applicationId", CLIENT_APPLICATION_ID);
// form.set("applicationUri", CLIENT_APPLICATION_URL);
// form.set("rd", "false");
// client.header("referer", EXPECTED_REDIRECT_URL);
// final Response response = client.form(form);
// Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());
// final String responseString = getResponse(response, RedirectResponse.class).getRedirectUrl();
// final String smsLinkAuthzCodeValue = responseString.split("&")[0].split("=")[1];
// Assert.assertEquals(
// "http://authn.drwp.com/#/sms/authentications?authzCode=" + smsLinkAuthzCodeValue
// +
// "&applicationId=PCC&applicationUri=http%3A%2F%2Flocalhost%3A9249%2Fapi%2Fgateway%2Floginredirect%3Fto%3D%2Fcdn%2Fdashboard%2Findex.html%23%2Fdashboard.html",
// responseString);
// final AuthorizationCode smsLinkAuthCode = authorizationCodeDao.getRecentAuthorizationCode(USERNAME,
// AuthorizationCodeType.SMS_2FLINK_CODE);
// Assert.assertEquals(smsLinkAuthzCodeValue, smsLinkAuthCode.getAuthCode());
// Assert.assertEquals(CLIENT_APPLICATION_ID, smsLinkAuthCode.getClientApplicationId());
// Assert.assertEquals(CLIENT_APPLICATION_SECRET, smsLinkAuthCode.getClientSecret());
// Assert.assertEquals(Boolean.TRUE, smsLinkAuthCode.getIsValidCode());
// Assert.assertEquals(MASTER_MID, smsLinkAuthCode.getMasterMid());
// Assert.assertEquals(USERNAME, smsLinkAuthCode.getUserName());
// Assert.assertEquals(AuthorizationCodeType.SMS_2FLINK_CODE, smsLinkAuthCode.getAuthzCodeType());
// Assert.assertEquals("127.0.0.1", smsLinkAuthCode.getClientIpAddress());
// Assert.assertEquals("Chrome-50.0.2661.102", smsLinkAuthCode.getClientBrowserInfo());
// Assert.assertEquals("Windows 7", smsLinkAuthCode.getClientOperatingSystem());
// Assert.assertEquals("Computer", smsLinkAuthCode.getClientDeviceType());
//
// SmsCode smsCode = smsCodeDao.getRecentSmsCode(USERNAME, CLIENT_APPLICATION_ID);
//
// initClient();
// client.path(CODE_PATH);
// client.header("referer", EXPECTED_REDIRECT_URL);
// final Response response2 = client.form(form);
// Assert.assertEquals(Status.OK.getStatusCode(), response2.getStatus());
// final String responseString2 = getResponse(response2, RedirectResponse.class).getRedirectUrl();
// final String smsLinkAuthzCodeValue2 = responseString2.split("&")[0].split("=")[1];
// Assert.assertNotEquals(smsLinkAuthzCodeValue, smsLinkAuthzCodeValue2);
//
// final Form formSmsCode = new Form();
// formSmsCode.set("authzCode", smsLinkAuthzCodeValue);
// formSmsCode.set("code", smsCode.getCode());
// formSmsCode.set("applicationId", CLIENT_APPLICATION_ID);
// formSmsCode.set("applicationUri", CLIENT_APPLICATION_URL);
//
// SmsCode smsCode2 = smsCodeDao.getRecentSmsCode(USERNAME, CLIENT_APPLICATION_ID);
// Assert.assertEquals(smsCode.getCode(), smsCode2.getCode());
//
// initClient();
// client.path("/v1/sms/authentications/code");
// client.header("referer", EXPECTED_REDIRECT_URL);
// final Response responseSmsCode = client.form(formSmsCode);
//
// Assert.assertEquals(Status.OK.getStatusCode(), responseSmsCode.getStatus());
//
// final String smsResponseRedirect = responseToMap(responseSmsCode).get("redirectUrl");
//
// Assert.assertTrue(smsResponseRedirect,
// smsResponseRedirect.contains("http://localhost:9249/api/gateway/loginredirect?code="));
// final String authorizationCodeValue = smsResponseRedirect.split("code=")[1].split("&")[0];
// final AuthorizationCode authzCode = authorizationCodeDao.getRecentAuthorizationCode(USERNAME,
// AuthorizationCodeType.LOGIN);
// Assert.assertEquals(authorizationCodeValue, authzCode.getAuthCode());
// // Validate Login Audit
// LoginAudit loginAudit = junitDao.getLoginAuditByMM(MASTER_MID, USERNAME).get(0);
// Assert.assertEquals("OK", loginAudit.getMessage());
// Assert.assertEquals("SUCCESS", loginAudit.getStatus());
// Assert.assertEquals(EXPECTED_REDIRECT_URL, loginAudit.getReferer());
// Assert.assertEquals("127.0.0.1", loginAudit.getIpAddress());
// Assert.assertEquals("Chrome-50.0.2661.102", loginAudit.getClientBrowserInfo());
// Assert.assertEquals("Windows 7", loginAudit.getClientOperatingSystem());
// Assert.assertEquals("Computer", loginAudit.getClientDeviceType());
// Assert.assertNull(smsCodeDao.getRecentSmsCode(USERNAME, CLIENT_APPLICATION_ID));
// }
// }
