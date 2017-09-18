package com.matteoveroni.vertxjavafxchatclient.events;

public class EventMessage {

    public final static String BUS_ADDRESS = "message_event";

    private final String text;

    public EventMessage(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
