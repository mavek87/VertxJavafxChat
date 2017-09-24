package com.matteoveroni.vertxjavafxchatbusinesslogic.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client.ClientPOJO;

public class ChatPrivateMessagePOJO {

    private final ClientPOJO sourceClient;
    private final ClientPOJO targetClient;
    private final String text;

    public ChatPrivateMessagePOJO(
        @JsonProperty("sourceClient") ClientPOJO sourceClient,
        @JsonProperty("targetClient") ClientPOJO targetClient,
        @JsonProperty("text") String text
    ) {
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
