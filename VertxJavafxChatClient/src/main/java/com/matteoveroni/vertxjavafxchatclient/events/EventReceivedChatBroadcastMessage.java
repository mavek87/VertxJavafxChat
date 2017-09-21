package com.matteoveroni.vertxjavafxchatclient.events;

import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ChatBroadcastMessagePOJO;

public class EventReceivedChatBroadcastMessage {

    public final static String BUS_ADDRESS = "received_chat_broadcast_message_event";

    private final ChatBroadcastMessagePOJO chatBroadcastMessage;

    public EventReceivedChatBroadcastMessage(ChatBroadcastMessagePOJO chatPrivateMessage) {
        this.chatBroadcastMessage = chatPrivateMessage;
    }

    public ChatBroadcastMessagePOJO getChatBroadcastMessage() {
        return chatBroadcastMessage;
    }
}
