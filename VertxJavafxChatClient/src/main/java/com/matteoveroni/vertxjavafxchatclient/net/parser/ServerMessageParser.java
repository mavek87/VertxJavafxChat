package com.matteoveroni.vertxjavafxchatclient.net.parser;

import com.google.gson.Gson;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ConnectionsUpdate;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ServerMessageType;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ServerMessage;
import io.vertx.core.buffer.Buffer;
import java.rmi.UnexpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerMessageParser {

    private static final Logger LOG = LoggerFactory.getLogger(ServerMessageParser.class);
    private static final Gson GSON = new Gson();

    public ServerMessage parse(Buffer buffer) throws Exception {

        final int HEADER_OFFSET = 4;
        LOG.info("Parsing a new network message - [buffer.length(): " + buffer.length() + "]");

        int messageHeader = buffer.getInt(0);
        LOG.info("ServerMessageHeader: " + messageHeader);

        String jsonString_message = buffer.getString(0 + HEADER_OFFSET, buffer.length());
        LOG.info("jsonString_serverMessage: " + jsonString_message);

        if (messageHeader == ServerMessageType.CONNECTION_STATE_CHANGE.getCode()) {

            ConnectionsUpdate connectionsState = GSON.fromJson(jsonString_message, ConnectionsUpdate.class);
            return new ServerMessage(ServerMessageType.CONNECTION_STATE_CHANGE, connectionsState);

        } else if (messageHeader == ServerMessageType.CHAT_MESSAGE.getCode()) {

            throw new UnsupportedOperationException();

        } else {

            throw new UnexpectedException("Unknown server message! Unable to parse it!");

        }
    }
}
