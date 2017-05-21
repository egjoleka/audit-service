package com.izettle.assignment.dao;
// package com.drwp.securityservice.authentication.dao;
//
// import static org.junit.Assert.assertEquals;
// import static org.junit.Assert.assertNotNull;
// import static org.junit.Assert.assertNull;
//
// import java.sql.Timestamp;
// import java.util.List;
//
// import org.joda.time.DateTime;
// import org.junit.Before;
// import org.junit.Test;
//
// import com.datastax.driver.core.exceptions.InvalidQueryException;
// import com.drwp.securityservice.authentication.BaseTest;
// import com.drwp.securityservice.authentication.entity.Application;
// import com.drwp.securityservice.authentication.exception.AuthenticationException;
//
// public class ApplicationDaoTest extends BaseTest {
//
// private Application application0;
// private Application application2;
// private Application application3;
// private Application application4;
// private final static String CLIENT_APPLICATION_ID_DISABLED = "pcc123";
// private final static Integer MAX_ALLOWED_REFRESH = 10;
//
// @Before
// public void warmingUp() {
// application0 = new Application();
// application0.setBearerExpirationInSeconds(BEARER_EXP_SECONDS);
// application0.setClientApplicationId(CLIENT_APPLICATION_ID);
// application0.setClientApplicationName(CLIENT_APPLICATION_NAME);
// application0.setClientSecretText(CLIENT_APPLICATION_SECRET);
// application0.setCreatedBy(CREATED_BY);
// application0.setCreatedTimestamp(new Timestamp(new DateTime().getMillis()));
// application0.setRefreshTokenExpirationInSeconds(REFRESH_EXP_SECONDS);
// application0.setUriText(CLIENT_APPLICATION_URL);
// application0.setMaxNumberOfRefreshTokens(MAX_ALLOWED_REFRESH);
// application0.setIsActive(Boolean.TRUE);
//
// application2 = new Application();
// application2.setBearerExpirationInSeconds(BEARER_EXP_SECONDS);
// application2.setClientApplicationId("CLIENT_APPLICATION_ID");
// application2.setClientApplicationName(CLIENT_APPLICATION_NAME);
// application2.setClientSecretText(CLIENT_APPLICATION_SECRET);
// application2.setCreatedBy(CREATED_BY);
// application2.setCreatedTimestamp(new Timestamp(new DateTime().getMillis()));
// application2.setRefreshTokenExpirationInSeconds(REFRESH_EXP_SECONDS);
// application2.setUriText("CLIENT_APPLICATION_URL");
// application2.setMaxNumberOfRefreshTokens(MAX_ALLOWED_REFRESH);
// application0.setIsActive(Boolean.TRUE);
//
// application3 = new Application();
// application3.setBearerExpirationInSeconds(BEARER_EXP_SECONDS);
// application3.setClientApplicationId(CLIENT_APPLICATION_ID_DISABLED);
// application3.setClientApplicationName(CLIENT_APPLICATION_NAME);
// application3.setClientSecretText(CLIENT_APPLICATION_SECRET);
// application3.setCreatedBy(CREATED_BY);
// application3.setCreatedTimestamp(new Timestamp(new DateTime().getMillis()));
// application3.setRefreshTokenExpirationInSeconds(REFRESH_EXP_SECONDS);
// application3.setUriText("CLIENT_APPLICATION_URL");
// application3.setMaxNumberOfRefreshTokens(MAX_ALLOWED_REFRESH);
// application3.setIsActive(Boolean.FALSE);
//
// application4 = new Application();
// application4.setBearerExpirationInSeconds(BEARER_EXP_SECONDS);
// application4.setClientApplicationId(CLIENT_APPLICATION_ID);
// application4.setClientApplicationName(CLIENT_APPLICATION_NAME);
// application4.setClientSecretText("CLIENT_APPLICATION_SECRET2");
// application4.setCreatedBy(CREATED_BY);
// application4.setCreatedTimestamp(new Timestamp(new DateTime().getMillis()));
// application4.setRefreshTokenExpirationInSeconds(REFRESH_EXP_SECONDS);
// application4.setUriText(CLIENT_APPLICATION_URL);
// application4.setMaxNumberOfRefreshTokens(MAX_ALLOWED_REFRESH);
// application4.setIsActive(Boolean.TRUE);
//
// junitDao.saveApplication(application0);
// junitDao.saveApplication(application2);
// junitDao.saveApplication(application3);
// junitDao.saveApplication(application4);
// }
//
// @Test
// public void testGetSuccess() {
// final Application application = applicationDao.getApplicationByApplicationIdAndAppSecret(CLIENT_APPLICATION_ID,
// CLIENT_APPLICATION_SECRET);
// assertNotNull(application);
// assertEquals(CLIENT_APPLICATION_ID, application.getClientApplicationId());
// assertEquals(CLIENT_APPLICATION_SECRET, application.getClientSecretText());
// assertEquals(CLIENT_APPLICATION_URL, application.getUriText());
// assertEquals(CLIENT_APPLICATION_NAME, application.getClientApplicationName());
// assertEquals(CREATED_BY, application.getCreatedBy());
// assertNull(application.getUpdatedTimestamp());
// assertEquals(BEARER_EXP_SECONDS, application.getBearerExpirationInSeconds());
// assertEquals(REFRESH_EXP_SECONDS, application.getRefreshTokenExpirationInSeconds());
// assertEquals(MAX_ALLOWED_REFRESH, application.getMaxNumberOfRefreshTokens());
// assertNotNull(application.getCreatedTimestamp());
// }
//
// @Test(expected = AuthenticationException.class)
// public void testGetDisabledSuccess() {
// final Application application = applicationDao
// .getApplicationByApplicationIdAndAppSecret(CLIENT_APPLICATION_ID_DISABLED, CLIENT_APPLICATION_SECRET);
// assertNotNull(application);
// assertEquals(CLIENT_APPLICATION_ID, application.getClientApplicationId());
// assertEquals(CLIENT_APPLICATION_SECRET, application.getClientSecretText());
// assertEquals(CLIENT_APPLICATION_URL, application.getUriText());
// assertEquals(CLIENT_APPLICATION_NAME, application.getClientApplicationName());
// assertEquals(CREATED_BY, application.getCreatedBy());
// assertNull(application.getUpdatedTimestamp());
// assertEquals(BEARER_EXP_SECONDS, application.getBearerExpirationInSeconds());
// assertEquals(REFRESH_EXP_SECONDS, application.getRefreshTokenExpirationInSeconds());
// assertEquals(MAX_ALLOWED_REFRESH, application.getMaxNumberOfRefreshTokens());
// assertNotNull(application.getCreatedTimestamp());
// }
//
// @Test
// public void testGetApplicationByIdSuccess() {
// final Application application = applicationDao.getApplicationByApplicationId(CLIENT_APPLICATION_ID).get(1);
// assertNotNull(application);
// assertEquals(CLIENT_APPLICATION_ID, application.getClientApplicationId());
// assertEquals(CLIENT_APPLICATION_SECRET, application.getClientSecretText());
// assertEquals(CLIENT_APPLICATION_URL, application.getUriText());
// assertEquals(CLIENT_APPLICATION_NAME, application.getClientApplicationName());
// assertEquals(CREATED_BY, application.getCreatedBy());
// assertNull(application.getUpdatedTimestamp());
// assertEquals(BEARER_EXP_SECONDS, application.getBearerExpirationInSeconds());
// assertEquals(REFRESH_EXP_SECONDS, application.getRefreshTokenExpirationInSeconds());
// assertEquals(MAX_ALLOWED_REFRESH, application.getMaxNumberOfRefreshTokens());
// assertNotNull(application.getCreatedTimestamp());
// }
//
// @Test(expected = AuthenticationException.class)
// public void testGetApplicationByIdDisabledSuccess() {
// applicationDao.getApplicationByApplicationId(CLIENT_APPLICATION_ID_DISABLED).get(0);
//
// }
//
// @Test(expected = AuthenticationException.class)
// public void testGetApplicationByIdNotFound() {
// applicationDao.getApplicationByApplicationId(" ");
// }
//
// @Test(expected = AuthenticationException.class)
// public void testGetNotFound() {
// final Application application = applicationDao
// .getApplicationByApplicationIdAndAppSecret("CLIENT_APPLICATION_ID", CLIENT_APPLICATION_URL);
// assertNotNull(application);
// assertEquals(CLIENT_APPLICATION_ID, application.getClientApplicationId());
// assertEquals(CLIENT_APPLICATION_SECRET, application.getClientSecretText());
// assertEquals(CLIENT_APPLICATION_URL, application.getUriText());
// assertEquals(CLIENT_APPLICATION_NAME, application.getClientApplicationName());
// assertEquals(CREATED_BY, application.getCreatedBy());
// assertEquals(null, application.getUpdatedBy());
// assertEquals(BEARER_EXP_SECONDS, application.getBearerExpirationInSeconds());
// assertEquals(REFRESH_EXP_SECONDS, application.getRefreshTokenExpirationInSeconds());
// assertEquals(MAX_ALLOWED_REFRESH, application.getMaxNumberOfRefreshTokens());
// assertNotNull(application.getCreatedTimestamp());
// }
//
// @Test
// public void testGetAllSuccess() {
// final List<Application> applications = applicationDao.getApplicationByApplicationId(CLIENT_APPLICATION_ID);
// assertEquals(2, applications.size());
// final Application application = applications.get(1);
// assertNotNull(application);
// assertEquals(CLIENT_APPLICATION_ID, application.getClientApplicationId());
// assertEquals(CLIENT_APPLICATION_SECRET, application.getClientSecretText());
// assertEquals(CLIENT_APPLICATION_URL, application.getUriText());
// assertEquals(CLIENT_APPLICATION_NAME, application.getClientApplicationName());
// assertEquals(CREATED_BY, application.getCreatedBy());
// assertEquals(null, application.getUpdatedTimestamp());
// assertEquals(BEARER_EXP_SECONDS, application.getBearerExpirationInSeconds());
// assertEquals(REFRESH_EXP_SECONDS, application.getRefreshTokenExpirationInSeconds());
// assertEquals(MAX_ALLOWED_REFRESH, application.getMaxNumberOfRefreshTokens());
// assertNotNull(application.getCreatedTimestamp());
// }
//
// @Test(expected = InvalidQueryException.class)
// public void testGetNullClientAppId() {
// applicationDao.getApplicationByApplicationIdAndAppSecret(CLIENT_APPLICATION_ID, null);
// }
// }
