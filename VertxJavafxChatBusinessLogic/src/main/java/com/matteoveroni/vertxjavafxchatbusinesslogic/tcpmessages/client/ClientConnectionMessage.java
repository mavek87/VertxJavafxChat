package com.matteoveroni.vertxjavafxchatbusinesslogic.tcpmessages.client;

import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ClientPOJO;
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
