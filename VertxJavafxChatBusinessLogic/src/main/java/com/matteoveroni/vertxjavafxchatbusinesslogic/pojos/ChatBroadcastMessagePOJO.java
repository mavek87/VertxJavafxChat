package com.matteoveroni.vertxjavafxchatbusinesslogic.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client.ClientPOJO;

public class ChatBroadcastMessagePOJO {

    private final ClientPOJO sourceClient;
    private final String text;

    public ChatBroadcastMessagePOJO(
        @JsonProperty("sourceClient") ClientPOJO sourceClient,
        @JsonProperty("text") String text
    ) {
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
