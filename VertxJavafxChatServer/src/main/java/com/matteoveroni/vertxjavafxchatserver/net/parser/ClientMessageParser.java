package com.matteoveroni.vertxjavafxchatserver.net.parser;

import com.google.gson.Gson;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client.ClientMessage;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client.ClientMessageType;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client.ClientPOJO;
import io.vertx.core.buffer.Buffer;
import java.rmi.UnexpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientMessageParser {

    private static final Logger LOG = LoggerFactory.getLogger(ClientMessageParser.class);
    private static final Gson GSON = new Gson();

    public ClientMessage parse(Buffer buffer) throws Exception {

        final int HEADER_OFFSET = 4;
        LOG.info("Parsing a new network message - [buffer.length(): " + buffer.length() + "]");

        int messageHeader = buffer.getInt(0);
        LOG.info("ClientMessageHeader: " + messageHeader);

        String jsonString_message = buffer.getString(0 + HEADER_OFFSET, buffer.length());
        LOG.info("jsonString_ClientMessage: " + jsonString_message);

        if (messageHeader == ClientMessageType.CLIENT_DISCONNECTION.getCode()) {
            
            ClientPOJO disconnectedClient = GSON.fromJson(jsonString_message, ClientPOJO.class);
            return new ClientMessage(ClientMessageType.CLIENT_DISCONNECTION, disconnectedClient);

        } else if (messageHeader == ClientMessageType.CLIENT_CHAT_PRIVATE_MESSAGE.getCode()) {

            throw new UnsupportedOperationException();

        } else {

            throw new UnexpectedException("Unknown client message! Unable to parse it!");

        }
    }
}
