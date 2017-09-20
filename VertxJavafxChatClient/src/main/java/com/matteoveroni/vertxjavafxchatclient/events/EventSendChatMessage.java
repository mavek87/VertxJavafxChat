package com.matteoveroni.vertxjavafxchatclient.events;

import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ChatPrivateMessagePOJO;

public class EventSendChatMessage {

    public final static String BUS_ADDRESS = "send_chat_message_event";

    private final ChatPrivateMessagePOJO chatPrivateMessage;

    public EventSendChatMessage(ChatPrivateMessagePOJO chatPrivateMessage) {
        this.chatPrivateMessage = chatPrivateMessage;
    }

    public ChatPrivateMessagePOJO getChatPrivateMessage() {
        return chatPrivateMessage;
    }

}
