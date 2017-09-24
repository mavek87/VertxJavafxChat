package com.matteoveroni.vertxjavafxchatbusinesslogic.tcpmessages;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ClientPOJO;

public class ChatPrivateMessage {

    private final ClientPOJO sourceClient;
    private final ClientPOJO targetClient;
    private final String text;

    public ChatPrivateMessage(
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
