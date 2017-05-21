package com.izettle.assignment.crypto;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Type;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.izettle.assignment.crypto.PasswordCrypto;
import com.izettle.assignment.exception.IzettleException;

public class PasswordCryptoTest {

    private static final Gson gson = new Gson();
    private static final String USER_NAME = "gjolekae@gmail.com";
    private static final String PASSWORD = "Edrin!@#01212";
    private static final String FIELD_NAME = "Password";

    @Test
    public void validatePassword_OK() {
        try {
            PasswordCrypto.validatePassword(USER_NAME, PASSWORD, FIELD_NAME);
        } catch (IzettleException ex) {
            Assert.fail("should not have failed");
            assertBadRequestExceptionResponse(ex.getResponse(), "New password should be at least 8 characters long");
        }
    }

    @Test
    public void validatePassword_SpecialCharcters() {
        PasswordCrypto.validatePassword(USER_NAME, "Password0:)", FIELD_NAME);
    }

    @Test
    public void validatePassword_MissingUserName() {
        try {
            PasswordCrypto.validatePassword(null, PASSWORD, FIELD_NAME);
            Assert.fail("should have failed");
        } catch (IzettleException ex) {
            assertBadRequestExceptionResponse(ex.getResponse(), "userName cannot be null or empty");
        }
    }

    @Test
    public void validatePassword_MissingPassword() {
        try {
            PasswordCrypto.validatePassword(USER_NAME, null, FIELD_NAME);
            Assert.fail("should have failed");
        } catch (IzettleException ex) {
            assertBadRequestExceptionResponse(ex.getResponse(), "Password should be at least 8 characters long");
        }
    }

    @Test
    public void validatePassword_ShortPassword() {
        try {
            PasswordCrypto.validatePassword(USER_NAME, "short", FIELD_NAME);
            Assert.fail("should have failed");
        } catch (IzettleException ex) {
            assertBadRequestExceptionResponse(ex.getResponse(), "Password should be at least 8 characters long");
        }
    }

    @Test
    public void validatePassword_TooLongPassword() {
        try {
            PasswordCrypto.validatePassword(USER_NAME,
                    "Password!@0Password!@0Password!@0Password!@0Password!@0Password!@0Password!@0Password!@0Password!@0Password!@0Password!@0Password!@0Password!@0Password!@0Password!@0",
                    FIELD_NAME);
            Assert.fail("should have failed");
        } catch (IzettleException ex) {
            assertBadRequestExceptionResponse(ex.getResponse(), "Password should not be longer than 160 characters");
        }
    }

    @Test
    public void validatePassword_InvalidPassword_MissingUpperCase() {
        try {
            PasswordCrypto.validatePassword(USER_NAME, "withoutupper0!", FIELD_NAME);
            Assert.fail("should have failed");
        } catch (IzettleException ex) {
            assertBadRequestExceptionResponse(ex.getResponse(),
                    "Password is not strong enough, should include lower case, upper case, digit and special characters");
        }
    }

    @Test
    public void validatePassword_InvalidPassword_MissingLowerCase() {
        try {
            PasswordCrypto.validatePassword(USER_NAME, "WITHOUTLOWER0!", FIELD_NAME);
            Assert.fail("should have failed");
        } catch (IzettleException ex) {
            assertBadRequestExceptionResponse(ex.getResponse(),
                    "Password is not strong enough, should include lower case, upper case, digit and special characters");
        }
    }

    @Test
    public void validatePassword_InvalidPassword_MissingDigit() {
        try {
            PasswordCrypto.validatePassword(USER_NAME, "WithoutUpper!", FIELD_NAME);
            Assert.fail("should have failed");
        } catch (IzettleException ex) {
            assertBadRequestExceptionResponse(ex.getResponse(),
                    "Password is not strong enough, should include lower case, upper case, digit and special characters");
        }
    }

    @Test
    public void validatePassword_InvalidPassword_MissingSpecialChar() {
        try {
            PasswordCrypto.validatePassword(USER_NAME, "WithoutUpper!", FIELD_NAME);
            Assert.fail("should have failed");
        } catch (IzettleException ex) {
            assertBadRequestExceptionResponse(ex.getResponse(),
                    "Password is not strong enough, should include lower case, upper case, digit and special characters");
        }
    }

    @Test
    public void validatePassword_InvalidPassword_SameAsUserName() {
        try {
            PasswordCrypto.validatePassword(USER_NAME, USER_NAME, FIELD_NAME);
            Assert.fail("should have failed");
        } catch (IzettleException ex) {
            assertBadRequestExceptionResponse(ex.getResponse(), "Password should not be similar to user name");
        }
    }

    @Test
    public void validatePassword_InvalidPassword_BigTraceOfUserName() {
        try {
            PasswordCrypto.validatePassword(USER_NAME, "!0egjoleka!0", FIELD_NAME);
            Assert.fail("should have failed");
        } catch (IzettleException ex) {
            assertBadRequestExceptionResponse(ex.getResponse(), "Password should not be similar to user name");
        }
    }

    @Test
    public void validatePassword_InvalidPassword_MediumTraceOfUserName() {
        try {
            PasswordCrypto.validatePassword(USER_NAME, "!0edrin!0", FIELD_NAME);
            Assert.fail("should have failed");
        } catch (IzettleException ex) {
            assertBadRequestExceptionResponse(ex.getResponse(), "Password is not strong enough, should include lower case, upper case, digit and special characters");
        }
    }

    @Test
    public void validatePassword_ValidPassword_SmallTraceOfUserName() {
        try {
            PasswordCrypto.validatePassword(USER_NAME, "!0edrW!0121", FIELD_NAME);
        } catch (@SuppressWarnings("unused") IzettleException ex) {
            Assert.fail("should not have failed");
        }
    }

    @Test
    public void validatePasswordReset_SamePassword() {
        try {
            PasswordCrypto.validatePasswordPolicy(USER_NAME, PASSWORD, PASSWORD);
        } catch (@SuppressWarnings("unused") IzettleException ex) {
            Assert.fail("should not have failed");
        }
    }

    @Test
    public void validatePasswordReset_DifferentPassword() {
        try {
            PasswordCrypto.validatePasswordPolicy(USER_NAME, PASSWORD, PASSWORD + "12");
            Assert.fail("should have failed");
        } catch (IzettleException ex) {
            assertBadRequestExceptionResponse(ex.getResponse(), "Password mismatched");
        }
    }

    protected void assertBadRequest(final Response response) {
        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(Status.BAD_REQUEST.getReasonPhrase(), response.getStatusInfo().getReasonPhrase());
    }

    protected void assertBadRequestExceptionResponse(final Response response, final String messsage) {
        assertBadRequest(response);
        final Map<String, String> responseAsJsonMap = jsonToMap((String) response.getEntity());
        assertEquals("invalid_request", responseAsJsonMap.get("error"));
        assertEquals(messsage, responseAsJsonMap.get("error_description"));
    }

    private Map<String, String> jsonToMap(final String jsonResponse) {
        final Type type = new TypeToken<Map<String, String>>() {}.getType();
        return gson.fromJson(jsonResponse, type);
    }
}
