package com.izettle.assignment.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.izettle.assignment.ddo.UserDisplayEntity;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
public interface LoginController {

    @GET
    @Path("/status")
    String isAlive();

    @POST
    @Path("/verification")
    Response signIn(@FormParam("user") final String username, @FormParam("password") final String password,
            @Context final HttpServletRequest httpServletRequest,
            @Context final HttpServletResponse httpServletResponse);

    @POST
    @Path("/registration")
    Response registerUser(final UserDisplayEntity userDisplayEntity, @Context final HttpServletResponse httpServletResponse);
}
