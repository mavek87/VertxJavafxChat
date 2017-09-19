package com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client;

public enum ClientMessageType {
    CLIENT_DISCONNECTION(0), CLIENT_CHAT_PRIVATE_MESSAGE(1);

    private final int code;

    ClientMessageType(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
