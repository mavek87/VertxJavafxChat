package com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client;

public class ClientMessage {

    private final ClientMessageType messageType;
    private final Object message;

    public ClientMessage(ClientMessageType messageType, Object message) {
        this.messageType = messageType;
        this.message = message;
    }

    public ClientMessageType getMessageType() {
        return messageType;
    }

    public Object getMessage() {
        return message;
    }

}
