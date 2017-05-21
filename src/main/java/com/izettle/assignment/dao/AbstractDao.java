package com.izettle.assignment.dao;

import static com.datastax.driver.core.querybuilder.QueryBuilder.delete;
import static com.datastax.driver.core.querybuilder.QueryBuilder.insertInto;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;
import static com.datastax.driver.core.querybuilder.QueryBuilder.update;

import java.sql.Timestamp;
import java.util.Date;

import com.datastax.driver.core.querybuilder.Delete;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.querybuilder.Update;
import com.izettle.assignment.AppConstants;

public class AbstractDao {

    protected Select getSelectStatement(final String tableName) {
        return select().all().from(AppConstants.IZETTLE_SVC_KEYSPACE, tableName);
    }

    protected Select getCheckSelectStatement(final String tableName) {
        return select().from(AppConstants.IZETTLE_SVC_KEYSPACE, tableName).limit(1);
    }

    protected Insert getInsertStatement(final String tableName) {
        return insertInto(AppConstants.IZETTLE_SVC_KEYSPACE, tableName);
    }

    protected Update getUpdateStatement(final String tableName) {
        return update(AppConstants.IZETTLE_SVC_KEYSPACE, tableName);
    }

    protected Delete getDeleteStatement(final String tableName) {
        return delete().from(AppConstants.IZETTLE_SVC_KEYSPACE, tableName);
    }

    protected Timestamp getTimestamp(final Date date) {
        return date != null ? new Timestamp(date.getTime()) : null;
    }
}
