package com.matteoveroni.vertxjavafxchatclient.events;

import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ChatBroadcastMessagePOJO;

public class EventSendChatBroadcastMessage {

    public final static String BUS_ADDRESS = "send_chat_broadcast_message_event";

    private final ChatBroadcastMessagePOJO chatBroadcastMessage;

    public EventSendChatBroadcastMessage(ChatBroadcastMessagePOJO chatBroadcastMessage) {
        this.chatBroadcastMessage = chatBroadcastMessage;
    }

    public ChatBroadcastMessagePOJO getChatBroadcastMessage() {
        return chatBroadcastMessage;
    }

}
