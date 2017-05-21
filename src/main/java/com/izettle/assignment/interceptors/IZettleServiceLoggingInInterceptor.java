package com.izettle.assignment.interceptors;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IZettleServiceLoggingInInterceptor extends AbstractPhaseInterceptor<Message> {

    public IZettleServiceLoggingInInterceptor() {
        this(Phase.RECEIVE);
    }

    public IZettleServiceLoggingInInterceptor(String phase) {
        super(phase);
    }

    private static final Logger cLogger = LoggerFactory.getLogger(IZettleServiceLoggingInInterceptor.class);

    @Override
    public void handleMessage(Message message) throws Fault {
        String url = (String) message.get(Message.REQUEST_URL);
        String httpRequestMethod = (String) message.get(Message.HTTP_REQUEST_METHOD);
        String contentType = (String) message.get(Message.CONTENT_TYPE);
        cLogger.debug("[REQUEST] RequestMethod: {}, URL: {}, Content-Type: {}", httpRequestMethod, url, contentType);
    }
}
