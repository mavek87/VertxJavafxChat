package com.matteoveroni.vertxjavafxchatserver.events;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EventServerDeploymentError {

    public static final String BUS_ADDRESS = "event_server_deployment_error";

    private final String exceptionMessage;

    public EventServerDeploymentError(@JsonProperty("exceptionMessage") String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }
}
