package com.matteoveroni.vertxjavafxchatbusinesslogic;

public enum CommunicationCode {
    CONNECTION_STATE_CHANGE(0), MESSAGE(1);

    private final int code;

    CommunicationCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
