package com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClientDisconnectionMessage {

    private final ClientPOJO disconnectedClient;

    public ClientDisconnectionMessage(@JsonProperty("disconnectedClient") ClientPOJO disconnectedClient) {
        this.disconnectedClient = disconnectedClient;
    }

    public ClientPOJO getDisconnectedClient() {
        return disconnectedClient;
    }
}
