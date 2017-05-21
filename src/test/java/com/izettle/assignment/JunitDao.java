package com.izettle.assignment;

import java.sql.Timestamp;
import java.util.Date;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;

public class JunitDao {

	private final Session session;

	private static PreparedStatement deleteUsers;
	private static PreparedStatement deleteAllAuditLogins;
	private static PreparedStatement deleteIssuedBearerTokens;

	public JunitDao(final Session session) {
		this.session = session;
		prepareStatements();
	}

	private void prepareStatements() {
		deleteUsers = session.prepare("TRUNCATE izettle_service.users");
		deleteAllAuditLogins = session.prepare("TRUNCATE izettle_service.login_audits");
		deleteIssuedBearerTokens = session.prepare("TRUNCATE izettle_service.issued_bearer_tokens");
	}

	public void wipeData() {
		wipeUsers();
		wipeLoginAuditData();
		wipeBearerTokens();
	}

	private void wipeBearerTokens() {
		session.execute(deleteIssuedBearerTokens.bind());
	}

	private void wipeLoginAuditData() {
		session.execute(deleteAllAuditLogins.bind());

	}

	public void wipeUsers() {
		session.execute(deleteUsers.bind());
	}

	protected Timestamp getNowTimestamp() {
		return new Timestamp(new Date().getTime());
	}

	private Timestamp getTimestamp(final Date date) {
		return date != null ? new Timestamp(date.getTime()) : null;
	}

}
