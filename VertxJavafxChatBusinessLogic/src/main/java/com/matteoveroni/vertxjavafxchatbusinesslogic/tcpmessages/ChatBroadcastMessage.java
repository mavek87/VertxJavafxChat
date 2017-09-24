package com.matteoveroni.vertxjavafxchatbusinesslogic.tcpmessages;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ClientPOJO;

public class ChatBroadcastMessage {

    private final ClientPOJO sourceClient;
    private final String text;

    public ChatBroadcastMessage(
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
