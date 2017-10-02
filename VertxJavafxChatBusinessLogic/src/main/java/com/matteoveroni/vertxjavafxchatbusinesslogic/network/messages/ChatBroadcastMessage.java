package com.matteoveroni.vertxjavafxchatbusinesslogic.network.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ClientPOJO;

@JsonPropertyOrder({"messageType", "sourceClient", "text"})
public class ChatBroadcastMessage implements NetworkMessage {

    private final NetworkMessageType messageType = NetworkMessageType.CHAT_BROADCAST_MESSAGE;

    private final ClientPOJO sourceClient;
    private final String text;

    public ChatBroadcastMessage(@JsonProperty("sourceClient") ClientPOJO sourceClient, @JsonProperty("text") String text) {
        this.sourceClient = sourceClient;
        this.text = text;
    }

    @Override
    public NetworkMessageType getMessageType() {
        return messageType;
    }

    public ClientPOJO getSourceClient() {
        return sourceClient;
    }

    public String getText() {
        return text;
    }

}
