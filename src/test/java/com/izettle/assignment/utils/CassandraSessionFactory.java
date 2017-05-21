/*
 * Copyright 2014 Digital River World Payments AB.
 */
package com.izettle.assignment.utils;

import org.cassandraunit.CassandraCQLUnit;
import org.cassandraunit.dataset.CQLDataSet;
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;

import com.datastax.driver.core.Session;

public class CassandraSessionFactory {

    public final static String KEYSPACE = "izettle_service";
    private static Session cassandraSession;

    static {
        loadSession();
    }

    private static void loadSession() {
        ClassPathCQLDataSet classPathCQLDataSet = new ClassPathCQLDataSet("simple.cql", KEYSPACE);
        CassandraCQLUnit cassandraCQLUnit = new BaseCassandraUnitTest(classPathCQLDataSet);
        cassandraSession = cassandraCQLUnit.session;
    }

    public static Session getSession() {
        if (cassandraSession == null || cassandraSession.isClosed()) {
            loadSession();
        }
        return cassandraSession;
    }

    static class BaseCassandraUnitTest extends CassandraCQLUnit {

        public BaseCassandraUnitTest(CQLDataSet dataSet) {
            super(dataSet);
            try {
                EmbeddedCassandraServerHelper.startEmbeddedCassandra("/cassandra.yaml");
                Thread.sleep(5000);
                super.load();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Session getNewSession() {
        ClassPathCQLDataSet classPathCQLDataSet = new ClassPathCQLDataSet("simple.cql", KEYSPACE);
        CassandraCQLUnit cassandraCQLUnit = new BaseCassandraUnitTest(classPathCQLDataSet);
        return cassandraCQLUnit.session;
    }
}
