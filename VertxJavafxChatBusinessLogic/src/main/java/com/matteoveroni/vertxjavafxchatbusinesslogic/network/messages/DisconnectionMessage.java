package com.matteoveroni.vertxjavafxchatbusinesslogic.network.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ClientPOJO;

@JsonPropertyOrder({"messageType", "disconnectedClient"})
public class DisconnectionMessage implements NetworkMessage {

    private final NetworkMessageType messageType = NetworkMessageType.CLIENT_DISCONNECTION;

    private final ClientPOJO disconnectedClient;

    public DisconnectionMessage(@JsonProperty("disconnectedClient") ClientPOJO disconnectedClient) {
        this.disconnectedClient = disconnectedClient;
    }

    @Override
    public NetworkMessageType getMessageType() {
        return messageType;
    }

    public ClientPOJO getDisconnectedClient() {
        return disconnectedClient;
    }

}
