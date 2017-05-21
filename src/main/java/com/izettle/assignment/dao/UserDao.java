package com.izettle.assignment.dao;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.izettle.assignment.utils.ExceptionCreator.*;

import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.izettle.assignment.entity.User;
import com.izettle.assignment.utils.ExceptionCreator;

public class UserDao extends AbstractDao {

	private static final String LAST_NAME = "last_name";
	private static final String FIRST_NAME = "first_name";
	private static final String CREATED_TIMESTAMP = "created_timestamp";
	private static final String IS_ACTIVE_USER = "is_active_user";
	private static final String SALT = "salt";
	private static final String PASSWORD = "password";
	private static final String USER_NAME = "user_name";
	private final Session session;
	private static final Logger cLogger = LoggerFactory.getLogger(UserDao.class);
	private final static String USER_TABLE = "users";

	public UserDao(final Session session) {
		this.session = session;
	}

	public void storeUser(final User user) {
		final Statement statement = getInsertStatement(USER_TABLE).value(USER_NAME, user.getUserName())
				.value(PASSWORD, user.getPassword()).value(SALT, user.getSalt())
				.value(IS_ACTIVE_USER, user.getIsActiveUser())
				.value(CREATED_TIMESTAMP, user.getCreatedTimestamp()).value(FIRST_NAME, user.getFirstName())
				.value(LAST_NAME, user.getLastName());

		session.execute(statement);
	}

	public User getUserByUsername(final String userName) {
		final Statement statement = getSelectStatement(USER_TABLE).where(eq(USER_NAME, userName));
		cLogger.debug("Trying to fetch the userEntity from database with username: {}", userName);
		ResultSet res = session.execute(statement);

		final int totalFound = res.getAvailableWithoutFetching();
		if (totalFound < 1) {
			cLogger.info("User {} does not exist", userName);
			throwBadRequestException("Not a valid user: " + userName);
		}

		final Row row = res.all().get(0);
		if (!row.getBool(IS_ACTIVE_USER)) {
			cLogger.info("User {} is not an active user", userName);
			throwBadRequestException("Not an valid user: " + userName);
		}
		cLogger.debug("UserEntity fetched successfully from database. The user ID: {}", userName);
		return createUserEntityFromDbResponse(row);
	}
	
	public boolean checkIfUserExists(final String userName) {
		final Statement statement = getSelectStatement(USER_TABLE).where(eq(USER_NAME, userName));
		cLogger.debug("Trying to fetch the userEntity from database with username: {}", userName);
		ResultSet res = session.execute(statement);

		final int totalFound = res.getAvailableWithoutFetching();
		if (totalFound < 1) {
			cLogger.info("User {} does not exist", userName);
			return false;
		}
		return true;
	}

	private User createUserEntityFromDbResponse(final Row row) {
		final User user = new User();
		user.setUserName(row.getString(USER_NAME));
		user.setPassword(row.getString(PASSWORD));
		user.setSalt(row.getString(SALT));
		user.setIsActiveUser(row.getBool(IS_ACTIVE_USER));
		user.setCreatedTimestamp(new Timestamp(row.getTimestamp(CREATED_TIMESTAMP).getTime()));
		user.setFirstName(row.getString(FIRST_NAME));
		user.setLastName(row.getString(LAST_NAME));
		return user;
	}

}
