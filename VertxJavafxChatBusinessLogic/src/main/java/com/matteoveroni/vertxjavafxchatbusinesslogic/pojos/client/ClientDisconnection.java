package com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client;

public class ClientDisconnection {

    private final ClientPOJO disconnectedClient;

    public ClientDisconnection(ClientPOJO disconnectedClient) {
        this.disconnectedClient = disconnectedClient;
    }

    public ClientPOJO getDisconnectedClient() {
        return disconnectedClient;
    }

}
