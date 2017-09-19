package com.matteoveroni.vertxjavafxchatbusinesslogic;

public enum NetworkMessageType {
    CONNECTION_STATE_CHANGE(0), MESSAGE(1);

    private final int code;

    NetworkMessageType(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
