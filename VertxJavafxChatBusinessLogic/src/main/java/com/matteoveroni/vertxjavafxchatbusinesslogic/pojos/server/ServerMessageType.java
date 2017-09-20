package com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.server;

public enum ServerMessageType {
    CONNECTION_STATE_CHANGE(0), SERVER_CHAT_PRIVATE_MESSAGE(1);

    private final int code;

    ServerMessageType(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
