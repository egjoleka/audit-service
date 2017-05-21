package com.izettle.assignment;

public interface AppConstants {

	long REFRESH_INTERVAL_MILLIS = 120000L;
	String IZETTLE_SVC_KEYSPACE = "izettle_service";

	String LOGIN_AUDIT_FAILED = "FAILURE";
	String LOGIN_AUDIT_SUCCESS = "SUCCESS";
	
	String OK = "OK";
	

	String STATUS_DOWN = "Status:DOWN";
	String STATUS_UP = "Status:UP";

	String BEARER = "Bearer";
	int CASSANDRA_TTL = 2678400;
	int TOKEN_EXP_MS = 3600000;

}
