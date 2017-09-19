package com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.server;

public class ServerMessage {

    private final ServerMessageType messageType;
    private final Object message;

    public ServerMessage(ServerMessageType messageType, Object message) {
        this.messageType = messageType;
        this.message = message;
    }

    public ServerMessageType getMessageType() {
        return messageType;
    }

    public Object getMessage() {
        return message;
    }

}
