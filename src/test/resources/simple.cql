CREATE TABLE izettle_service.users ( 
 	user_name text,
	password text,
	first_name text,
	last_name text,
	salt text,
	is_active_user boolean,
        created_timestamp timestamp,
	PRIMARY KEY (user_name)
);

Create TABLE izettle_service.login_audits (
 user_name text,
    is_success boolean,
    req_time_uuid timeuuid,
    bearer text,
    client_browser_info text,
    client_device_type text,
    client_ip_address text,
    client_operating_system text,
    reason text,
    request_timestamp timestamp,
    status text,
  Primary key ((user_name,is_success), req_time_uuid)
) WITH CLUSTERING ORDER BY (req_time_uuid DESC);



CREATE TABLE izettle_service.issued_bearer_tokens ( 
 	   bearer text,
    bearer_ts timeuuid,
    created_timestamp timestamp,
    expiration_timestamp timestamp,
    is_valid_bearer boolean,
    user_name text,
	PRIMARY KEY ((bearer), bearer_ts)
);