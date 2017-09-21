package com.matteoveroni.vertxjavafxchatclient.events;

import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ChatPrivateMessagePOJO;

public class EventSendChatPrivateMessage {

    public final static String BUS_ADDRESS = "send_chat_private_message_event";

    private final ChatPrivateMessagePOJO chatPrivateMessage;

    public EventSendChatPrivateMessage(ChatPrivateMessagePOJO chatPrivateMessage) {
        this.chatPrivateMessage = chatPrivateMessage;
    }

    public ChatPrivateMessagePOJO getChatPrivateMessage() {
        return chatPrivateMessage;
    }

}
