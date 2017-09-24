package com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClientConnectionMessage {

    private final ClientPOJO connectedClient;

    public ClientConnectionMessage(@JsonProperty("connectedClient") ClientPOJO connectedClient) {
        this.connectedClient = connectedClient;
    }

    public ClientPOJO getConnectedClient() {
        return connectedClient;
    }
}
