package com.izettle.assignment.dao;

public enum SecBearerTokenPermission {
    TOKEN_BEARER("bearer_token"),
    USER_ID("user_id"),
    CLIENT_ID("client_id"),
    PROTECTED_RESOURCE("protected_resource"),
    RESOURCE_OPERATION("resource_operation"),
    CREATE_TIMESTAMP("create_timestamp"),
    BEARER_MAX_TOKEN_USAGE("bearer_token_max_usage"),
    BEARER_EXPIRATION("bearer_token_expiration_time");

    private String value;

    private SecBearerTokenPermission(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
