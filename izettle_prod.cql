CREATE KEYSPACE izettle_service WITH REPLICATION = {'class' : 'SimpleStrategy', 'replication_factor' : 1};
CREATE TABLE users ( 
 	user_name text,
	password text,
	first_name text,
	last_name text,
	salt text,
	is_active_user boolean,
        create_timestamp timestamp,
	PRIMARY KEY (user_name)
);

Create TABLE login_audits (
  user_name text,
  is_success boolean,
  req_time_uuid timeuuid,
  request_timestamp timestamp,
  client_ip_address text,
  client_browser_info text,
  client_operating_system text,
  client_device_type text,
  status text,
  reason text,
  bearer text,
  Primary key ((user_name,is_success), req_time_uuid)
) WITH CLUSTERING ORDER BY (req_time_uuid DESC);



CREATE TABLE issued_bearer_tokens ( 
 	bearer text,
 	bearer_ts timeuuid,
 	user_name text,
	is_valid boolean,
	create_timestamp timestamp,
	expiration_timestamp timestamp,
	PRIMARY KEY ((bearer), bearer_ts)
);