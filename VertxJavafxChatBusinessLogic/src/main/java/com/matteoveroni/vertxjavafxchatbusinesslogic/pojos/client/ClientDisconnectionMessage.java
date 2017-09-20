package com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client;

public class ClientDisconnectionMessage extends ClientMessage {

    public ClientDisconnectionMessage(ClientPOJO disconnectedClient) {
        super(ClientMessageType.CLIENT_DISCONNECTION, disconnectedClient);
    }

}
