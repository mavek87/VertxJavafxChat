package com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client;

public class ClientConnectionMessage {

    private final ClientPOJO connectedClient;

    public ClientConnectionMessage(ClientPOJO connectedClient) {
        this.connectedClient = connectedClient;
    }

    public ClientPOJO getConnectedClient() {
        return connectedClient;
    }
}
