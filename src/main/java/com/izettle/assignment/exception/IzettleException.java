package com.izettle.assignment.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class IzettleException extends WebApplicationException {

    private static final long serialVersionUID = -1778556706865357928L;

    public IzettleException(Response message) {
        super(message);
    }

}