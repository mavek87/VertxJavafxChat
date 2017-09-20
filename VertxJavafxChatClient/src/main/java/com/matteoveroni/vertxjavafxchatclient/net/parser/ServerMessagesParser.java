package com.matteoveroni.vertxjavafxchatclient.net.parser;

import com.google.gson.Gson;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ChatPrivateMessagePOJO;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.server.ServerConnectionsUpdateMessage;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.server.ServerMessageType;
import io.vertx.core.buffer.Buffer;
import java.rmi.UnexpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerMessagesParser {

    private static final Logger LOG = LoggerFactory.getLogger(ServerMessagesParser.class);
    private static final Gson GSON = new Gson();

    public Object parse(Buffer buffer) throws Exception {

        final int HEADER_OFFSET = 4;
        LOG.info("Parsing a new network message - [buffer.length(): " + buffer.length() + "]");

        int messageHeader = buffer.getInt(0);
        LOG.info("ServerMessageHeader: " + messageHeader);

        String jsonString_message = buffer.getString(0 + HEADER_OFFSET, buffer.length());
        LOG.info("jsonString_serverMessage: " + jsonString_message);

        if (messageHeader == ServerMessageType.CONNECTION_STATE_CHANGE.getCode()) {
            
            return GSON.fromJson(jsonString_message, ServerConnectionsUpdateMessage.class);
            
        } else if (messageHeader == ServerMessageType.SERVER_CHAT_PRIVATE_MESSAGE.getCode()) {
            
            ChatPrivateMessagePOJO chatPrivateMessage = GSON.fromJson(jsonString_message, ChatPrivateMessagePOJO.class);
            return chatPrivateMessage;
            
        } else {
            
            throw new UnexpectedException("Unknown server message code! Not possible to parse server message!");

        }
    }
}
