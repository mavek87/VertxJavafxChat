package com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client;

public class ClientChatPrivateMessage extends ClientMessage {

    public ClientChatPrivateMessage(ClientPOJO sourceClient, ClientPOJO targetClient, String privateMessageText) {
        super(ClientMessageType.CLIENT_CHAT_PRIVATE_MESSAGE, privateMessageText);
    }
}
