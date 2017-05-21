package com.izettle.assignment.interceptors;

import javax.servlet.http.HttpServletResponse;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IZettleServiceLoggingOutInterceptor extends AbstractPhaseInterceptor<Message> {

    private final Logger cLogger = LoggerFactory.getLogger(IZettleServiceLoggingOutInterceptor.class);

    public IZettleServiceLoggingOutInterceptor() {
        this(Phase.PRE_STREAM);
    }

    public IZettleServiceLoggingOutInterceptor(String phase) {
        super(phase);
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        HttpServletResponse response = (HttpServletResponse) message.getExchange().getOutMessage()
                .get(AbstractHTTPDestination.HTTP_RESPONSE);
        String contentType = message.get(Message.CONTENT_TYPE).toString();
        cLogger.debug("[RESPONSE] ResponseCode: {}, Content-Type: {}", response.getStatus(), contentType);
    }

}
