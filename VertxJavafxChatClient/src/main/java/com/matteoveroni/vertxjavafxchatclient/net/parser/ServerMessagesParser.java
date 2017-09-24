package com.matteoveroni.vertxjavafxchatclient.net.parser;

import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ChatBroadcastMessagePOJO;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ChatPrivateMessagePOJO;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.server.ServerConnectionsUpdateMessage;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.server.ServerMessageType;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import java.rmi.UnexpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerMessagesParser {

    private static final Logger LOG = LoggerFactory.getLogger(ServerMessagesParser.class);

    public Object parse(Buffer buffer) throws Exception {

        final int HEADER_OFFSET = 4;
        LOG.info("NEW MESSAGE - Parsing a new network message - [buffer.length(): " + buffer.length() + "]");

        int messageHeader = buffer.getInt(0);
        LOG.info("ServerMessageHeader: " + messageHeader);

        String jsonString_message = buffer.getString(0 + HEADER_OFFSET, buffer.length());
        LOG.info("ServerMessage: " + jsonString_message);
        
        JsonObject json_message = new JsonObject(jsonString_message);

        if (messageHeader == ServerMessageType.CONNECTION_STATE_CHANGE.getCode()) {
            
            return json_message.mapTo(ServerConnectionsUpdateMessage.class);

        } else if (messageHeader == ServerMessageType.SERVER_CHAT_PRIVATE_MESSAGE.getCode()) {

            return json_message.mapTo(ChatPrivateMessagePOJO.class);

        } else if (messageHeader == ServerMessageType.SERVER_CHAT_BROADCAST_MESSAGE.getCode()) {

            return json_message.mapTo(ChatBroadcastMessagePOJO.class);

        } else {

            throw new UnexpectedException("Unknown server message code! Not possible to parse server message!");

        }
    }
}
