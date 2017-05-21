package com.izettle.assignment;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;

public class StaticResourceHandler extends ResourceHandler {
    private static final String CSP = "default-src 'none'; script-src 'self' ; object-src 'none'; style-src 'self' 'unsafe-inline'; img-src 'self'; media-src 'none'; "
            + "frame-src 'none'; font-src 'self'; connect-src 'self'";

    @Override
    protected void doResponseHeaders(HttpServletResponse response, Resource resource, String mimeType) {
        super.doResponseHeaders(response, resource, mimeType);
        response.setHeader("Content-Security-Policy", CSP);
        response.setHeader("X-Content-Security-Policy", CSP);
        response.setHeader("X-WebKit-CSP", CSP);
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("X-Content-Type-Options", "nosniff");
    }
}
