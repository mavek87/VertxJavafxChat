package com.matteoveroni.vertxjavafxchatserver.events;

public class EventServerDeploymentError {

    private final String exceptionMessage;

    public EventServerDeploymentError(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }
}
