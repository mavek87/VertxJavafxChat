package com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client;

public class ClientDisconnectionMessage {

    private final ClientPOJO disconnectedClient;

    public ClientDisconnectionMessage(ClientPOJO disconnectedClient) {
        this.disconnectedClient = disconnectedClient;
    }

    public ClientPOJO getDisconnectedClient() {
        return disconnectedClient;
    }
}
