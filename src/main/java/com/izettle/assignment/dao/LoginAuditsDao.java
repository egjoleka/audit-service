package com.izettle.assignment.dao;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.ttl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.izettle.assignment.AppConstants;
import com.izettle.assignment.entity.LoginAudit;
import com.izettle.assignment.utils.ExceptionCreator;

public class LoginAuditsDao extends AbstractDao {

	private static final String USER_NAME = "user_name";
	private static final String STATUS = "status";
	private static final String REQUEST_TIMESTAMP = "request_timestamp";
	private static final String REQ_TIME_UUID = "req_time_uuid";
	private static final String REASON = "reason";
	private static final String IS_SUCCESS = "is_success";
	private static final String CLIENT_OPERATING_SYSTEM = "client_operating_system";
	private static final String CLIENT_IP_ADDRESS = "client_ip_address";
	private static final String CLIENT_DEVICE_TYPE = "client_device_type";
	private static final String CLIENT_BROWSER_INFO = "client_browser_info";
	private static final String BEARER = "bearer";
	private static final Logger cLogger = LoggerFactory.getLogger(LoginAuditsDao.class);
	private final Session session;
	private final static String LOGIN_AUDITS_TABLE = "login_audits";

	public LoginAuditsDao(final Session session) {
		this.session = session;
	}

	public void store(final LoginAudit loginAudit) {
		final Statement statement = getInsertStatement(LOGIN_AUDITS_TABLE).value(USER_NAME, loginAudit.getUserName())
				.value(IS_SUCCESS, loginAudit.getIsSucess()).value(REQ_TIME_UUID, loginAudit.getReqTimeUuid())
				.value(REQUEST_TIMESTAMP, getNowTimestamp())
				.value(CLIENT_IP_ADDRESS, loginAudit.getClientIpAddress())
				.value(CLIENT_BROWSER_INFO, loginAudit.getClientBrowserInfo())
				.value(CLIENT_OPERATING_SYSTEM, loginAudit.getClientOperatingSystem())
				.value(CLIENT_DEVICE_TYPE, loginAudit.getClientDeviceType()).value(STATUS, loginAudit.getStatus())
				.value(REASON, loginAudit.getReason()).value(BEARER, loginAudit.getBearer()).using(ttl(AppConstants.CASSANDRA_TTL));
		session.execute(statement);
	}

	public List<LoginAudit> getLoginAuditsByUsernameAndStatus(final String username, final boolean isSuccess) {
		final Statement statement = getSelectStatement(LOGIN_AUDITS_TABLE).where(eq(USER_NAME, username))
				.and(eq(IS_SUCCESS, isSuccess)).setFetchSize(10);
		final ResultSet res = session.execute(statement);
		final int totalFound = res.getAvailableWithoutFetching();
		cLogger.debug("Found total Login Audits: {}", totalFound);
		if (totalFound < 1) {
			cLogger.info("There are no Login Audits for user: {}", username);
			ExceptionCreator.throwBadRequestException("There are no Login Audits for user: " + username);
		}
		final List<LoginAudit> loginAudits = new ArrayList<>();
		int paginator = 5;
		if(totalFound<5) {
			paginator = totalFound;
		}
		final List<Row> rows = res.all().subList(0, paginator);
		rows.stream().forEach(row -> {
			loginAudits.add(createLoginAuditEntityFromDbResponse(row));
		});

		return loginAudits;
	}

	private LoginAudit createLoginAuditEntityFromDbResponse(final Row row) {

		final LoginAudit loginAudit = new LoginAudit();
		loginAudit.setBearer(row.getString(BEARER));
		loginAudit.setClientBrowserInfo(row.getString(CLIENT_BROWSER_INFO));
		loginAudit.setClientDeviceType(row.getString(CLIENT_DEVICE_TYPE));
		loginAudit.setClientIpAddress(row.getString(CLIENT_IP_ADDRESS));
		loginAudit.setClientOperatingSystem(row.getString(CLIENT_OPERATING_SYSTEM));
		loginAudit.setIsSucess(row.getBool(IS_SUCCESS));
		loginAudit.setReason(row.getString(REASON));
		loginAudit.setReqTimeUuid(row.getUUID(REQ_TIME_UUID));
		loginAudit.setRequestTimestamp(getTimestamp(row.getTimestamp(REQUEST_TIMESTAMP)));
		loginAudit.setStatus(row.getString(STATUS));
		loginAudit.setUserName(row.getString(USER_NAME));
		return loginAudit;
	}

	protected Timestamp getNowTimestamp() {
		return new Timestamp(new Date().getTime());
	}
}
