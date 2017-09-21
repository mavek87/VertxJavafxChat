package com.matteoveroni.vertxjavafxchatbusinesslogic.pojos;

import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client.ClientPOJO;

public class ChatPrivateMessagePOJO {

    private final ClientPOJO sourceClient;
    private final ClientPOJO targetClient;
    private final String text;

    public ChatPrivateMessagePOJO(ClientPOJO sourceClient, ClientPOJO targetClient, String text) {
        this.sourceClient = sourceClient;
        this.targetClient = targetClient;
        this.text = text;
    }

    public ClientPOJO getSourceClient() {
        return sourceClient;
    }

    public ClientPOJO getTargetClient() {
        return targetClient;
    }

    public String getText() {
        return text;
    }

}
