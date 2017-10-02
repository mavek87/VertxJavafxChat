package com.matteoveroni.vertxjavafxchatbusinesslogic.network.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ClientPOJO;

@JsonPropertyOrder({"messageType", "sourceClient", "targetClient", "text"})
public class ChatPrivateMessage implements NetworkMessage {

    private final NetworkMessageType messageType = NetworkMessageType.CHAT_PRIVATE_MESSAGE;

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

    @Override
    public NetworkMessageType getMessageType() {
        return messageType;
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
