package com.matteoveroni.vertxjavafxchatclient.events;

import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ChatPrivateMessagePOJO;

public class EventReceivedChatPrivateMessage {

    public final static String BUS_ADDRESS = "received_chat_private_message_event";

    private final ChatPrivateMessagePOJO chatPrivateMessage;

    public EventReceivedChatPrivateMessage(ChatPrivateMessagePOJO chatPrivateMessage) {
        this.chatPrivateMessage = chatPrivateMessage;
    }

    public ChatPrivateMessagePOJO getChatPrivateMessage() {
        return chatPrivateMessage;
    }
}
