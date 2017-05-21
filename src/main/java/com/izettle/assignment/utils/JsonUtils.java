package com.izettle.assignment.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Utility class for encoding/decoding/json
 *
 * @author egjoleka
 *
 */
public class JsonUtils {

    static public final String DELIMITER = ".";

    public static String convertToBase64(final String source) {
        return Base64.encodeBase64URLSafeString(StringUtils.getBytesUtf8(source));
    }

    public static String convertBytesToBase64(final byte[] source) {
        return Base64.encodeBase64URLSafeString(source);
    }

    public static String convertStringToBase64(final String source) {
        return Base64.encodeBase64URLSafeString(source.getBytes());
    }

    public static String decodeFromBase64String(final String encoded) {
        return new String(Base64.decodeBase64(encoded));
    }

    public static byte[] decodeFromBase64Bytes(final String encoded) {
        return Base64.decodeBase64(encoded);
    }

    public static String fromBase64ToJsonString(final String source) {
        return StringUtils.newStringUtf8(Base64.decodeBase64(source));
    }

    public static String toJson(final JsonObject json) {
        return new Gson().toJson(json);
    }

    public static void isBase64(final String base64) {
        if (!Base64.isBase64(base64)) {
            ExceptionCreator.throwBadRequestException("The jwt is not correctly encoded in base64 format");
        }
    }

    public static void isJSONValid(final String jsonElement) {
        try {
            final Gson gson = new Gson();
            gson.fromJson(jsonElement, Object.class);
        } catch (@SuppressWarnings("unused") Exception ex) {
            ExceptionCreator.throwBadRequestException("JWT does not contain a valid JSON format.");
        }
    }

}
