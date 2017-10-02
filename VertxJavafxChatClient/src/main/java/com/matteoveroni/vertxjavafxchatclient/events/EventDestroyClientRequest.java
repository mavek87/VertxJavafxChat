package com.matteoveroni.vertxjavafxchatclient.events;

public class EventDestroyClientRequest {

    public final static String BUS_ADDRESS = "event_client_shutdown_request";

    public Exception exceptionOccurred;

    public EventDestroyClientRequest() {
    }

    public EventDestroyClientRequest(Exception exceptionOccurred) {
        this.exceptionOccurred = exceptionOccurred;
    }
    
    public boolean isSomeExceptionOccurred() {
        return exceptionOccurred != null;
    }

    public Exception getExceptionOccurred() {
        return exceptionOccurred;
    }
}
