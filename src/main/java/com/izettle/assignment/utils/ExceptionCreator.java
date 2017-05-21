package com.izettle.assignment.utils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.izettle.assignment.exception.IzettleException;

public class ExceptionCreator {

    private static final String NOT_FOUND = "not_found";
	private static final String SERVER_ERROR = "server_error";
	private static final String ERROR_DESCRIPTION = "error_description";
	private static final String INVALID_REQUEST = "invalid_request";
	private static final String ERROR = "error";
	private static Logger logger = LoggerFactory.getLogger(ExceptionCreator.class);

    public static void throwBadRequestException(final String message) {
        final JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty(ERROR, INVALID_REQUEST);
        jsonResponse.addProperty(ERROR_DESCRIPTION, message);
        throw new IzettleException(
                Response.status(Status.BAD_REQUEST).entity(getJsonResponse(jsonResponse)).build());
    }

    public static void throwForbiddenException(final String message) {
        final JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty(ERROR, INVALID_REQUEST);
        jsonResponse.addProperty(ERROR_DESCRIPTION, message);
        throw new IzettleException(
                Response.status(Status.FORBIDDEN).entity(getJsonResponse(jsonResponse)).build());
    }

    public static void throwUnauthorizedException(final String message) {
        final JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty(ERROR, INVALID_REQUEST);
        jsonResponse.addProperty(ERROR_DESCRIPTION, message);
        throw new IzettleException(
                Response.status(Status.UNAUTHORIZED).entity(getJsonResponse(jsonResponse)).build());
    }

    public static void throwInternalServerError(final String message) {
        final JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty(ERROR, SERVER_ERROR);
        jsonResponse.addProperty(ERROR_DESCRIPTION, message);
        throw new IzettleException(
                Response.status(Status.INTERNAL_SERVER_ERROR).entity(getJsonResponse(jsonResponse)).build());
    }

    public static void throwInternalNotFoundException(final String message) {
        final JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty(ERROR, NOT_FOUND);
        jsonResponse.addProperty(ERROR_DESCRIPTION, message);
        throw new IzettleException(
                Response.status(Status.NOT_FOUND).entity(getJsonResponse(jsonResponse)).build());
    }

    private static String getJsonResponse(final JsonObject jsonResponse) {
        String json = JsonUtils.toJson(jsonResponse);
        logger.info("Returning error response: {}", json);
        return json;
    }
}
