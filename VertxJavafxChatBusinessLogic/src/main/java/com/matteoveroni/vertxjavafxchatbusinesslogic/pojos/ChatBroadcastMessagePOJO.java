package com.matteoveroni.vertxjavafxchatbusinesslogic.pojos;

import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client.ClientPOJO;

public class ChatBroadcastMessagePOJO {

    private final ClientPOJO sourceClient;
    private final String text;

    public ChatBroadcastMessagePOJO(ClientPOJO sourceClient, String text) {
        this.sourceClient = sourceClient;
        this.text = text;
    }

    public ClientPOJO getSourceClient() {
        return sourceClient;
    }

    public String getText() {
        return text;
    }

}
