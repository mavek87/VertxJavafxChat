package com.matteoveroni.vertxjavafxchatclient.events;

public class EventGUIShutdown {

    private final String exceptionDescription;

    public EventGUIShutdown(String exceptionDescription) {
        this.exceptionDescription = exceptionDescription;
    }

    public String getExceptionDescription() {
        return exceptionDescription;
    }
}
