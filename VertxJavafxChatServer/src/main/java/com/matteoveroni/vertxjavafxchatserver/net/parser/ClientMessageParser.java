package com.matteoveroni.vertxjavafxchatserver.net.parser;

import com.matteoveroni.vertxjavafxchatbusinesslogic.tcpmessages.ChatBroadcastMessage;
import com.matteoveroni.vertxjavafxchatbusinesslogic.tcpmessages.ChatPrivateMessage;
import com.matteoveroni.vertxjavafxchatbusinesslogic.tcpmessages.client.ClientConnectionMessage;
import com.matteoveroni.vertxjavafxchatbusinesslogic.tcpmessages.client.ClientDisconnectionMessage;
import com.matteoveroni.vertxjavafxchatbusinesslogic.tcpmessages.client.ClientMessageType;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import java.rmi.UnexpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientMessageParser {

    private static final Logger LOG = LoggerFactory.getLogger(ClientMessageParser.class);

    public Object parse(Buffer buffer) throws Exception {

        final int HEADER_OFFSET = 4;
        LOG.info("NEW MESSAGE - Parsing a new network message - [buffer.length(): " + buffer.length() + "]");

        int messageHeader = buffer.getInt(0);
        LOG.info("ClientMessageHeader: " + messageHeader);

        String jsonString_message = buffer.getString(0 + HEADER_OFFSET, buffer.length());
        LOG.info("ClientMessage: " + jsonString_message);

        JsonObject jsonObject = new JsonObject(jsonString_message);

        if (messageHeader == ClientMessageType.CLIENT_CONNECTION.getCode()) {

            return jsonObject.mapTo(ClientConnectionMessage.class);
            
        } else if (messageHeader == ClientMessageType.CLIENT_DISCONNECTION.getCode()) {

            return jsonObject.mapTo(ClientDisconnectionMessage.class);

        } else if (messageHeader == ClientMessageType.CLIENT_CHAT_PRIVATE_MESSAGE.getCode()) {
            
            return jsonObject.mapTo(ChatPrivateMessage.class);
            
        } else if (messageHeader == ClientMessageType.CLIENT_CHAT_BROADCAST_MESSAGE.getCode()) {
            
            return jsonObject.mapTo(ChatBroadcastMessage.class);
            
        } else {

            throw new UnexpectedException("Unknown client message! Unable to parse it!");

        }
    }
}
