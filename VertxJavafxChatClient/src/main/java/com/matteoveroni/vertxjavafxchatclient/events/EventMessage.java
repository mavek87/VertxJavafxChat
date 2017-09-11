package com.matteoveroni.vertxjavafxchatclient.events;

public class EventMessage {

    public final static String BUS_EVENT_MESSAGE_ADDRESS = "bus_address_event";

    private final String text;

    public EventMessage(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
