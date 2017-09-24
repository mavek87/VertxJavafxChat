package com.matteoveroni.vertxjavafxchatbusinesslogic.tcpmessages.client;

public enum ClientMessageType {
    CLIENT_CONNECTION(0), CLIENT_DISCONNECTION(1), CLIENT_CHAT_PRIVATE_MESSAGE(2), CLIENT_CHAT_BROADCAST_MESSAGE(3);

    private final int code;

    ClientMessageType(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
