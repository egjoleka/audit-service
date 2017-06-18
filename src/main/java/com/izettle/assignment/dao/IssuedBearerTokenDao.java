package com.izettle.assignment.dao;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.ttl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.izettle.assignment.AppConstants;
import com.izettle.assignment.entity.IssuedBearerToken;
import com.izettle.assignment.utils.ExceptionCreator;

public class IssuedBearerTokenDao extends AbstractDao {

	private static final String BEARER_TS = "bearer_ts";
	private static final String EXPIRATION_TIME = "expiration_timestamp";
	private static final String CREATED_TIMESTAMP = "created_timestamp";
	private static final String USER_NAME = "user_name";
	private static final String IS_VALID_BEARER = "is_valid_bearer";
	private static final String BEARER = "bearer";
	private final Session session;
	private static final Logger cLogger = LoggerFactory.getLogger(IssuedBearerTokenDao.class);
	private final static String ISSUED_BEARER_TOKEN_TABLE = "issued_bearer_tokens";

	public IssuedBearerTokenDao(final Session session) {
		this.session = session;
	}

	public void createBearerTokens(IssuedBearerToken issuedBearerToken) {
		final Statement statement = getInsertStatement(ISSUED_BEARER_TOKEN_TABLE)
				.value(BEARER, issuedBearerToken.getIssuedBearerToken())
				.value(IS_VALID_BEARER, issuedBearerToken.getIsValidBearer())
				.value(USER_NAME, issuedBearerToken.getUserName())
				.value(CREATED_TIMESTAMP, issuedBearerToken.getCreatedTimestamp())
				.value(EXPIRATION_TIME, issuedBearerToken.getExpirationTime())
				.value(BEARER_TS, issuedBearerToken.getBearerTs())
				.using(ttl(AppConstants.CASSANDRA_TTL));

		cLogger.debug("Trying to persist bearer token for user: {}", issuedBearerToken.getUserName());
		session.execute(statement);

	}

	public IssuedBearerToken getBearerTokensByBearerTokenId(final String issuedBearerToken) {
		final Statement statement = getSelectStatement(ISSUED_BEARER_TOKEN_TABLE)
				.where(eq(BEARER, issuedBearerToken));
		final ResultSet res = session.execute(statement);
		final int totalFound = res.getAvailableWithoutFetching();
		cLogger.debug("Found total IssuedBearerToken records: {}", totalFound);
		if (totalFound < 1) {
			cLogger.info("Not a valid issuedBearerToken: {} ", issuedBearerToken);
			ExceptionCreator.throwBadRequestException("Not a valid Bearer Token: " + issuedBearerToken);
		}
		cLogger.debug("The total number of issuedBearerToken is > 0: " + totalFound);
		final Row row = res.all().get(0);

		return createIssuedBearerTokenEntityFromDbResponse(row);

	}

	private IssuedBearerToken createIssuedBearerTokenEntityFromDbResponse(final Row row) {
		final IssuedBearerToken issuedBearerTokenEntity = new IssuedBearerToken();
		issuedBearerTokenEntity.setIssuedBearerToken(row.getString(BEARER));
		issuedBearerTokenEntity.setIsValidBearer(row.getBool(IS_VALID_BEARER));
		issuedBearerTokenEntity.setUserName(row.getString(USER_NAME));
		issuedBearerTokenEntity.setCreatedTimestamp(getTimestamp(row.getTimestamp(CREATED_TIMESTAMP)));
		issuedBearerTokenEntity.setExpirationTime(getTimestamp(row.getTimestamp("expiration_timestamp")));
		issuedBearerTokenEntity.setBearerTs(row.getUUID(BEARER_TS));
		return issuedBearerTokenEntity;
	}

	public void validateCassandraAccess() {
		final Statement statement = getCheckSelectStatement(ISSUED_BEARER_TOKEN_TABLE);
		session.execute(statement);
	}
}
