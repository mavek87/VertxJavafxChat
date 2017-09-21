package com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client;

public enum ClientMessageType {
    CLIENT_CONNECTION(0), CLIENT_DISCONNECTION(1), CLIENT_CHAT_PRIVATE_MESSAGE(2);

    private final int code;

    ClientMessageType(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
