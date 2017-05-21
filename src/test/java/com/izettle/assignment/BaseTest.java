package com.izettle.assignment;

import static org.junit.Assert.assertEquals;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.PropertyConfigurator;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.BeforeClass;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.UUIDs;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.izettle.assignment.dao.IssuedBearerTokenDao;
import com.izettle.assignment.dao.LoginAuditsDao;
import com.izettle.assignment.dao.UserDao;
import com.izettle.assignment.entity.IssuedBearerToken;
import com.izettle.assignment.entity.LoginAudit;
import com.izettle.assignment.entity.User;
import com.izettle.assignment.utils.CassandraSessionFactory;

public class BaseTest {

	protected final static String CREATED_BY = "edrin";

	protected static final String USER_NAME2 = "egjoleka@test.gmail.com";
	protected static final Timestamp createdTimestamp = new Timestamp(new DateTime().getMillis());
	protected static final Timestamp updatedTimestamp = new Timestamp(new DateTime().getMillis() + 1000000);
	protected static final String USERNAME = "gjolekae@gmail.com";
	protected static final String PASSWORD = "Informatika13?";
	protected static final String FIRST_NAME = "edrin";
	protected static final String LAST_NAME = "gjoleka";
	protected static final String BEARER = "111111111111111111111111";
	protected static final String ISO_DATETIME_TIME_ZONE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZZ";
	protected static final Gson gson = new GsonBuilder().setDateFormat(ISO_DATETIME_TIME_ZONE_FORMAT).create();
	protected static LoginAuditsDao loginAuditsDao;
	protected static UserDao userDao;
	protected static IssuedBearerTokenDao issuedBearerTokenDao;
	private final List<LoginAudit> audits = new ArrayList<>();
	protected static JunitDao junitDao;
	protected static PropertiesConfiguration cfg;

	@BeforeClass
	public static void beforeClass() throws ConfigurationException {
		final String filePath = BaseTest.class.getClassLoader().getResource("test.properties").getFile();
		cfg = new PropertiesConfiguration(filePath);
		PropertyConfigurator.configure(filePath);
		Session session = CassandraSessionFactory.getSession();
		loginAuditsDao = new LoginAuditsDao(session);
		userDao = new UserDao(session);
		issuedBearerTokenDao = new IssuedBearerTokenDao(session);
		junitDao = new JunitDao(session);

	}

	@Before
	public void cleanUp() {
		junitDao.wipeData();
	}

	protected static Session getSession() {
		return CassandraSessionFactory.getSession();
	}

	protected static String getStringResponse(final Response response) {
		try {
			return IOUtils.toString((InputStream) response.getEntity());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	protected List<LoginAudit> createLoginAudits() {
		audits.add(LoginAudit.createInstance(USERNAME, AppConstants.OK, AppConstants.OK, BEARER, Boolean.TRUE));
		audits.add(LoginAudit.createInstance(USERNAME, AppConstants.OK, AppConstants.OK, BEARER + "2", Boolean.TRUE));
		return audits;
	}

	protected IssuedBearerToken createTestBearerToken() {
		final IssuedBearerToken issuedBearerToken = new IssuedBearerToken();
		issuedBearerToken.setBearerTs(UUIDs.timeBased());
		issuedBearerToken.setCreatedTimestamp(getNowTimestamp());
		issuedBearerToken.setExpirationTime(getExpirationTimestamp());
		issuedBearerToken.setIssuedBearerToken(BEARER);
		issuedBearerToken.setIsValidBearer(Boolean.FALSE);
		issuedBearerToken.setUserName(USERNAME);
		return issuedBearerToken;
	}

//	protected User createUser(final String userName) {
//		final User user = new User();
//		user.setCreatedTimestamp(getNowTimestamp());
//		user.setFirstName(FIRST_NAME);
//		user.setLastName(LAST_NAME);
//		user.setIsActiveUser(Boolean.TRUE);
//		user.setPassword(ENCRYPTED_PASSWORD);
//		user.setSalt(SALT);
//		user.setUserName(userName);
//
//		return user;
//	}


	protected void assertRedirectRequest(final Response response, final String message) {
		assertRedirectRequest(response, message, "invalid_request");
	}

	protected void assertRedirectRequest(final Response response, final String message, final String error) {
		assertRedirectRequest(response);
		final URL location = getLocationHeader(response);
		final String jsonReponse = getJsonErrorResponse(location);
		final Map<String, String> responseAsJsonMap = jsonToMap(jsonReponse);
		assertEquals(error, responseAsJsonMap.get("error"));
		assertEquals(message, responseAsJsonMap.get("error_description"));
	}

	private URL getLocationHeader(final Response response) {
		try {
			final String location = response.getHeaderString("location");
			byte[] decodeUrl = URLCodec.decodeUrl(location.getBytes());
			return new URL(new String(decodeUrl));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String getJsonErrorResponse(final URL location) {
		final String query = location.getQuery();
		int startIndex = query.indexOf("{");
		int endIndex = query.indexOf("}", startIndex);
		return query.substring(startIndex, endIndex + 1);
	}

	private void assertRedirectRequest(final Response response) {
		assertEquals(Status.SEE_OTHER.getStatusCode(), response.getStatus());
		assertEquals(Status.SEE_OTHER.getReasonPhrase(), response.getStatusInfo().getReasonPhrase());
	}

	protected <T> T getResponse(final Response response, final Class<T> clz) {
		try {
			final String jsonResponse = IOUtils.toString((InputStream) response.getEntity());
			System.out.println(jsonResponse);
			return gson.fromJson(jsonResponse, clz);
		} catch (final Exception ex) {
			
			throw new RuntimeException(ex);
		}
	}

	protected Map<String, String> responseToMap(final Response response) {
		final Type type = new TypeToken<Map<String, String>>() {
		}.getType();
		return gson.fromJson(getStringResponse(response), type);
	}

	private Map<String, String> jsonToMap(final String jsonResponse) {
		final Type type = new TypeToken<Map<String, String>>() {
		}.getType();
		return gson.fromJson(jsonResponse, type);
	}


	protected void assertBadRequest(final Response response, final String message) {
		assertBadRequest(response);
		final Map<String, String> responseAsJsonMap = jsonToMap(getStringResponse(response));
		assertEquals("invalid_request", responseAsJsonMap.get("error"));
		assertEquals(message, responseAsJsonMap.get("error_description"));
	}

	protected void assertServerError(final Response response, final String message) {
		assertEquals(Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
		assertEquals(Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), response.getStatusInfo().getReasonPhrase());
		final Map<String, String> responseAsJsonMap = jsonToMap(getStringResponse(response));
		assertEquals("server_error", responseAsJsonMap.get("error"));
		assertEquals(message, responseAsJsonMap.get("error_description"));
	}

	protected void assertBadRequestExceptionResponse(final Response response, final String messsage) {
		assertBadRequest(response);
		final Map<String, String> responseAsJsonMap = jsonToMap((String) response.getEntity());
		assertEquals("invalid_request", responseAsJsonMap.get("error"));
		assertEquals(messsage, responseAsJsonMap.get("error_description"));
	}

	private void assertBadRequest(final Response response) {
		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
		assertEquals(Status.BAD_REQUEST.getReasonPhrase(), response.getStatusInfo().getReasonPhrase());
	}
	
	private Timestamp getNowTimestamp() {
		return new Timestamp(new Date().getTime());
	}

	private Timestamp getExpirationTimestamp() {
		return new Timestamp(new Date().getTime() + AppConstants.TOKEN_EXP_MS);
	}

}
