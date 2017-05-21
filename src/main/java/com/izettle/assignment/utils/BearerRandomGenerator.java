package com.izettle.assignment.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.izettle.assignment.exception.IzettleException;

public class BearerRandomGenerator {

    private static final Logger cLogger = LoggerFactory.getLogger(BearerRandomGenerator.class);

    /**
     * Generate the unique value of the token bearer based in the UUID.
     *
     * @return
     */
    public String generateValue() {
        try {
            String token = generateValue(UUID.randomUUID().toString());
            if (StringUtils.isBlank(token)) {
                ExceptionCreator.throwBadRequestException("Could not generate token value");
            }
            return token;
        } catch (NoSuchAlgorithmException e) {
            cLogger.error("Platform is missing SHA1 algorithm.", e);
            throw new IzettleException(Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Platform is missing SHA1 algorithm.").build());
        }
    }

    protected String generateValue(String param) throws NoSuchAlgorithmException {
        cLogger.debug("Creating the unique bearer value");
        final MessageDigest algorithm = MessageDigest.getInstance("SHA1");
        algorithm.reset();
        algorithm.update(param.getBytes());
        byte[] messageDigest = algorithm.digest();
        final StringBuilder hexString = new StringBuilder();
        for (byte element : messageDigest) {
            hexString.append(Integer.toHexString(0xFF & element));
        }
        cLogger.debug("Unique bearer created successfully!");
        return hexString.toString();
    }
}
