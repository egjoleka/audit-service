package com.izettle.assignment.integration;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.ext.form.Form;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.izettle.assignment.BaseTest;
import com.izettle.assignment.IzettleAssignmentMain;
import com.izettle.assignment.ddo.AuditsDisplayEntity;
import com.izettle.assignment.ddo.BearerTokenDisplayEntity;
import com.izettle.assignment.ddo.UserDisplayEntity;
import com.izettle.assignment.entity.LoginAudit;

public class RestApiIntegrationTest extends BaseTest {

	private static final String PASSWORD_FIELD = "password";
	private static final String USERNAME_FIELD = "user";
	private WebClient client;
	private final static String ENDPOINT_ADDRESS = "http://localhost:" + IzettleAssignmentMain.REST_PORT;
	protected static Server server;
	private final static String USER_VERIFY_PATH = "/auth/verification";
	private final static String REGISTRATION_PATH = "/auth/registration";
	private final static String STATUS_PATH = "/auth/status";
	private final static String STATUS_RESPONSE = "Status:UP";
	private UserDisplayEntity userDisplayEntity;
	private UserDisplayEntity userDisplayEntity2;
	private final static String AUDITS_PATH = "/audits";

	@BeforeClass
	public static void warmUp() throws Exception {
		server = IzettleAssignmentMain.startRestServices(IzettleAssignmentMain.REST_PORT, getSession());
	}

	@Before
	public void setUp() {
		initClient();
		
		userDisplayEntity = new UserDisplayEntity(USERNAME, PASSWORD, FIRST_NAME, LAST_NAME, PASSWORD);
		userDisplayEntity2 = new UserDisplayEntity(USER_NAME2, PASSWORD, FIRST_NAME, LAST_NAME, PASSWORD);
		
	}

	@AfterClass
	public static void destroy() {
		if (server != null) {
			server.stop();
			server.destroy();
		}
	}

	private void initClient() {
		List<Object> providers = new ArrayList<Object>();
		providers.add(new JacksonJaxbJsonProvider());
		client = WebClient.create(ENDPOINT_ADDRESS, providers);
		client.accept(MediaType.APPLICATION_JSON);
		
		client.header("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko)Chrome/50.0.2661.102 Safari/537.36");

	}

	@Test
	public void testIsAlive() {
		client.path(STATUS_PATH);
		Assert.assertEquals(STATUS_RESPONSE, client.get(String.class));
	}

	@Test
	public void verifyUser_Fail_NoUserExists() {
		client.type(MediaType.APPLICATION_FORM_URLENCODED);
		client.path(USER_VERIFY_PATH);
		final Form form = new Form();
		form.set(USERNAME_FIELD, BaseTest.USERNAME);
		form.set(PASSWORD_FIELD, BaseTest.PASSWORD);

		final Response response = client.form(form);
		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());

	}
	
	@Test
	public void registerUserFirstTime() {
		client.type(MediaType.APPLICATION_JSON);
		client.path(REGISTRATION_PATH);
		final Response response = client.post(userDisplayEntity);
		Assert.assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void reRegisterUserFailure() {
		client.type(MediaType.APPLICATION_JSON);
		client.path(REGISTRATION_PATH);
		final Response response = client.post(userDisplayEntity);
		Assert.assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
		final Response response2 = client.post(userDisplayEntity);
		Assert.assertEquals(Status.BAD_REQUEST.getStatusCode(), response2.getStatus());
	}
	
	@Test
	public void verifyUserSuccess() {
		client.type(MediaType.APPLICATION_JSON);
		client.path(REGISTRATION_PATH);
		final Response response = client.post(userDisplayEntity);
		Assert.assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
		
		client.reset();
		
		client.type(MediaType.APPLICATION_FORM_URLENCODED);
		client.path(USER_VERIFY_PATH);
		final Form form = new Form();
		form.set(USERNAME_FIELD, BaseTest.USERNAME);
		form.set(PASSWORD_FIELD, BaseTest.PASSWORD);

		final Response response2 = client.form(form);
		Assert.assertEquals(Status.OK.getStatusCode(), response2.getStatus());
		final BearerTokenDisplayEntity bearerTokenDisplayEntity = getResponse(response2, BearerTokenDisplayEntity.class);
		Assert.assertNotNull(bearerTokenDisplayEntity);
		Assert.assertNotNull(bearerTokenDisplayEntity.getBearerToken());
		Assert.assertNotNull(bearerTokenDisplayEntity.getExpirationTimestamp());
	}
	
	@Test
	public void verifyUserWrongPasswordFailure() {
		client.type(MediaType.APPLICATION_JSON);
		client.path(REGISTRATION_PATH);
		final Response response = client.post(userDisplayEntity);
		Assert.assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
		
		client.reset();
		
		client.type(MediaType.APPLICATION_FORM_URLENCODED);
		client.path(USER_VERIFY_PATH);
		final Form form = new Form();
		form.set(USERNAME_FIELD, BaseTest.USERNAME);
		form.set(PASSWORD_FIELD, "BaseTest.PASSWORD");

		final Response response2 = client.form(form);
		Assert.assertEquals(Status.UNAUTHORIZED.getStatusCode(), response2.getStatus());
		
	}
	
	@Test
	public void verifyAuditsOnlyOneUserOnlySuccessSuccess() {
		client.type(MediaType.APPLICATION_JSON);
		client.path(REGISTRATION_PATH);
		final Response response = client.post(userDisplayEntity);
		Assert.assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
		
		client.reset();
		
		client.type(MediaType.APPLICATION_FORM_URLENCODED);
		client.path(USER_VERIFY_PATH);
		final Form form = new Form();
		form.set(USERNAME_FIELD, BaseTest.USERNAME);
		form.set(PASSWORD_FIELD, BaseTest.PASSWORD);

		final Response response2 = client.form(form);
		Assert.assertEquals(Status.OK.getStatusCode(), response2.getStatus());
		
		final Response response3 = client.form(form);
		Assert.assertEquals(Status.OK.getStatusCode(), response3.getStatus());
		
		final Response response4 = client.form(form);
		Assert.assertEquals(Status.OK.getStatusCode(), response4.getStatus());
		
		final BearerTokenDisplayEntity bearerTokenDisplayEntity = getResponse(response2, BearerTokenDisplayEntity.class);
		Assert.assertNotNull(bearerTokenDisplayEntity);
		Assert.assertNotNull(bearerTokenDisplayEntity.getBearerToken());
		Assert.assertNotNull(bearerTokenDisplayEntity.getExpirationTimestamp());
		
		client.reset();
		client.path(AUDITS_PATH);
		client.accept(MediaType.APPLICATION_JSON);
		client.query("bearer", bearerTokenDisplayEntity.getBearerToken());
		client.query("isSuccess", Boolean.TRUE);

		final AuditsDisplayEntity auditsDisplayEntityResponse = client.get(AuditsDisplayEntity.class);
		Assert.assertNotNull(auditsDisplayEntityResponse);
		final List<LoginAudit> audits = auditsDisplayEntityResponse.getLoginAudits();
		Assert.assertEquals(3, audits.size());
		Assert.assertEquals(Boolean.TRUE, audits.get(0).getIsSucess());
		Assert.assertEquals(Boolean.TRUE, audits.get(1).getIsSucess());
		Assert.assertEquals(Boolean.TRUE, audits.get(2).getIsSucess());
		
		
	}
	
	@Test
	public void verifyAuditsOnlyOneUserOnlySuccessAndFailure() {
		client.type(MediaType.APPLICATION_JSON);
		client.path(REGISTRATION_PATH);
		final Response response = client.post(userDisplayEntity);
		Assert.assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
		
		client.reset();
		
		client.type(MediaType.APPLICATION_FORM_URLENCODED);
		client.path(USER_VERIFY_PATH);
		final Form form = new Form();
		form.set(USERNAME_FIELD, BaseTest.USERNAME);
		form.set(PASSWORD_FIELD, BaseTest.PASSWORD);

		final Response response2 = client.form(form);
		Assert.assertEquals(Status.OK.getStatusCode(), response2.getStatus());
		
		final Response response3 = client.form(form);
		Assert.assertEquals(Status.OK.getStatusCode(), response3.getStatus());
		
		final Response response4 = client.form(form);
		Assert.assertEquals(Status.OK.getStatusCode(), response4.getStatus());
		
		final Form form2 = new Form();
		form2.set(USERNAME_FIELD, BaseTest.USERNAME);
		form2.set(PASSWORD_FIELD, "BaseTest.PASSWORD");
		final Response response5 = client.form(form2);
		Assert.assertEquals(Status.UNAUTHORIZED.getStatusCode(), response5.getStatus());
		
		final BearerTokenDisplayEntity bearerTokenDisplayEntity = getResponse(response2, BearerTokenDisplayEntity.class);
		Assert.assertNotNull(bearerTokenDisplayEntity);
		Assert.assertNotNull(bearerTokenDisplayEntity.getBearerToken());
		Assert.assertNotNull(bearerTokenDisplayEntity.getExpirationTimestamp());
		
		client.reset();
		client.path(AUDITS_PATH);
		client.accept(MediaType.APPLICATION_JSON);
		client.query("bearer", bearerTokenDisplayEntity.getBearerToken());
		client.query("isSuccess", Boolean.TRUE);

		final AuditsDisplayEntity auditsDisplayEntityResponse = client.get(AuditsDisplayEntity.class);
		Assert.assertNotNull(auditsDisplayEntityResponse);
		final List<LoginAudit> audits = auditsDisplayEntityResponse.getLoginAudits();
		Assert.assertEquals(3, audits.size());
		Assert.assertEquals(Boolean.TRUE, audits.get(0).getIsSucess());
		Assert.assertEquals(Boolean.TRUE, audits.get(1).getIsSucess());
		Assert.assertEquals(Boolean.TRUE, audits.get(2).getIsSucess());
		
		client.reset();
		client.path(AUDITS_PATH);
		client.accept(MediaType.APPLICATION_JSON);
		client.query("bearer", bearerTokenDisplayEntity.getBearerToken());
		client.query("bearer", bearerTokenDisplayEntity.getBearerToken());
		client.query("isSuccess", Boolean.FALSE);
		
		final AuditsDisplayEntity auditsDisplayEntityResponse2 = client.get(AuditsDisplayEntity.class);
		Assert.assertNotNull(auditsDisplayEntityResponse2);
		final List<LoginAudit> audits2 = auditsDisplayEntityResponse2.getLoginAudits();
		Assert.assertEquals(1, audits2.size());
		Assert.assertEquals(Boolean.FALSE, audits2.get(0).getIsSucess());
	}
	
	@Test
	public void verifyAuditsTwoUserSuccessAndFailure() {
		client.type(MediaType.APPLICATION_JSON);
		client.path(REGISTRATION_PATH);
		final Response response = client.post(userDisplayEntity);
		Assert.assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
		
		final Response responseUser2 = client.post(userDisplayEntity2);
		Assert.assertEquals(Status.CREATED.getStatusCode(), responseUser2.getStatus());
		
		client.reset();
		
		client.type(MediaType.APPLICATION_FORM_URLENCODED);
		client.path(USER_VERIFY_PATH);
		final Form form = new Form();
		form.set(USERNAME_FIELD, BaseTest.USERNAME);
		form.set(PASSWORD_FIELD, BaseTest.PASSWORD);

		final Response response2 = client.form(form);
		Assert.assertEquals(Status.OK.getStatusCode(), response2.getStatus());
		
		final Response response3 = client.form(form);
		Assert.assertEquals(Status.OK.getStatusCode(), response3.getStatus());
		
		final Response response4 = client.form(form);
		Assert.assertEquals(Status.OK.getStatusCode(), response4.getStatus());
		
		final Form form2 = new Form();
		form2.set(USERNAME_FIELD, BaseTest.USERNAME);
		form2.set(PASSWORD_FIELD, "BaseTest.PASSWORD");
		final Response response5 = client.form(form2);
		Assert.assertEquals(Status.UNAUTHORIZED.getStatusCode(), response5.getStatus());
		
		final BearerTokenDisplayEntity bearerTokenDisplayEntity = getResponse(response2, BearerTokenDisplayEntity.class);
		Assert.assertNotNull(bearerTokenDisplayEntity);
		Assert.assertNotNull(bearerTokenDisplayEntity.getBearerToken());
		Assert.assertNotNull(bearerTokenDisplayEntity.getExpirationTimestamp());
		
		final Form formUser2 = new Form();
		formUser2.set(USERNAME_FIELD, BaseTest.USER_NAME2);
		formUser2.set(PASSWORD_FIELD, BaseTest.PASSWORD);
		final Response responseUser2Verify = client.form(formUser2);
		Assert.assertEquals(Status.OK.getStatusCode(), responseUser2Verify.getStatus());
		
		final BearerTokenDisplayEntity bearerTokenDisplayEntityUser2 = getResponse(responseUser2Verify, BearerTokenDisplayEntity.class);
		Assert.assertNotNull(bearerTokenDisplayEntityUser2);
		Assert.assertNotNull(bearerTokenDisplayEntityUser2.getBearerToken());
		Assert.assertNotNull(bearerTokenDisplayEntityUser2.getExpirationTimestamp());
		
		client.reset();
		client.path(AUDITS_PATH);
		client.accept(MediaType.APPLICATION_JSON);
		client.query("bearer", bearerTokenDisplayEntity.getBearerToken());
		client.query("isSuccess", Boolean.TRUE);

		final AuditsDisplayEntity auditsDisplayEntityResponse = client.get(AuditsDisplayEntity.class);
		Assert.assertNotNull(auditsDisplayEntityResponse);
		final List<LoginAudit> audits = auditsDisplayEntityResponse.getLoginAudits();
		Assert.assertEquals(3, audits.size());
		Assert.assertEquals(Boolean.TRUE, audits.get(0).getIsSucess());
		Assert.assertEquals(Boolean.TRUE, audits.get(1).getIsSucess());
		Assert.assertEquals(Boolean.TRUE, audits.get(2).getIsSucess());
		
		client.reset();
		client.path(AUDITS_PATH);
		client.accept(MediaType.APPLICATION_JSON);
		client.query("bearer", bearerTokenDisplayEntity.getBearerToken());
		client.query("bearer", bearerTokenDisplayEntity.getBearerToken());
		client.query("isSuccess", Boolean.FALSE);
		
		final AuditsDisplayEntity auditsDisplayEntityResponse2 = client.get(AuditsDisplayEntity.class);
		Assert.assertNotNull(auditsDisplayEntityResponse2);
		final List<LoginAudit> audits2 = auditsDisplayEntityResponse2.getLoginAudits();
		Assert.assertEquals(1, audits2.size());
		Assert.assertEquals(Boolean.FALSE, audits2.get(0).getIsSucess());
		
		client.reset();
		client.path(AUDITS_PATH);
		client.accept(MediaType.APPLICATION_JSON);
		client.query("bearer", bearerTokenDisplayEntityUser2.getBearerToken());
		client.query("isSuccess", Boolean.TRUE);
		
		final AuditsDisplayEntity auditsDisplayEntityResponseUser2 = client.get(AuditsDisplayEntity.class);
		Assert.assertNotNull(auditsDisplayEntityResponseUser2);
		final List<LoginAudit> auditsUser2 = auditsDisplayEntityResponseUser2.getLoginAudits();
		Assert.assertEquals(1, auditsUser2.size());
		Assert.assertEquals(Boolean.TRUE, auditsUser2.get(0).getIsSucess());
		Assert.assertEquals(BaseTest.USER_NAME2, auditsUser2.get(0).getUserName());
	}
	
	

}
