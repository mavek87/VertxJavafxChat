package com.matteoveroni.vertxjavafxchatbusinesslogic.network.messages;

import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ClientPOJO;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"messageType", "connectedClient"})
public class ConnectionMessage implements NetworkMessage {

    private final NetworkMessageType messageType = NetworkMessageType.CLIENT_CONNECTION;

    private final ClientPOJO connectedClient;

    public ConnectionMessage(@JsonProperty("connectedClient") ClientPOJO connectedClient) {
        this.connectedClient = connectedClient;
    }

    @Override
    public NetworkMessageType getMessageType() {
        return messageType;
    }

    public ClientPOJO getConnectedClient() {
        return connectedClient;
    }

}
