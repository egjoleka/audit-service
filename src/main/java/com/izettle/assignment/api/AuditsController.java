package com.izettle.assignment.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.izettle.assignment.ddo.AuditsDisplayEntity;

@Path("/audits")
@Produces(MediaType.APPLICATION_JSON)
public interface AuditsController {

    @GET
    @Path("/")
    AuditsDisplayEntity getAudits(@QueryParam("bearer") final String bearer, @QueryParam("isSuccess") boolean isSuccess);

}
