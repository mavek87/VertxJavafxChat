package com.matteoveroni.vertxjavafxchatclient.events;

public class EventChatMessage {

    public final static String BUS_ADDRESS = "message_event";

    private final String text;

    public EventChatMessage(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
