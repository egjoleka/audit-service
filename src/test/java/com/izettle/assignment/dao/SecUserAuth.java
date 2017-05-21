package com.izettle.assignment.dao;

public enum SecUserAuth {
    USER_ID("user_id"),
    CREATE_TIMESTAMP("create_timestamp"),
    KEY_ONE("key_one"),
    KEY_ONE_TIMESTAMP("key_one_timestamp"),
    KEY_TWO("key_two"),
    KEY_TWO_TIMESTAMP("key_two_timestamp"),
    BEARER_TOKEN_VALIDITY_ATTEMP("bearer_token_validity_attempts"),
    BEARER_TOKEN_VALIDITY_SECONDS("bearer_token_validity_max_seconds"),
    MERCHANT_IDS("merchant_ids"),
    IS_ACTIVE("isActive");

    private String value;

    private SecUserAuth(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
