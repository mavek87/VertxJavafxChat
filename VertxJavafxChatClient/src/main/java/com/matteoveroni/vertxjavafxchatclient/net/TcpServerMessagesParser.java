package com.matteoveroni.vertxjavafxchatclient.net;

import com.google.gson.Gson;
import com.matteoveroni.vertxjavafxchatbusinesslogic.NetworkMessageType;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ConnectionsUpdatePOJO;
import com.matteoveroni.vertxjavafxchatclient.events.EventConnectionsUpdate;
import io.vertx.core.buffer.Buffer;
import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpServerMessagesParser {

    private static final Logger LOG = LoggerFactory.getLogger(TcpServerMessagesParser.class);

    public void parseMessage(Buffer buffer) {
        final int HEADER_OFFSET = 8;

        int bufferIndex = 0;

        try {

            LOG.info("Parsing a new network message, buffer.length(): " + buffer.length());
            while (bufferIndex < buffer.length()) {

                int networkMessageCode = buffer.getInt(bufferIndex);
                LOG.info("networkMessageCode: " + networkMessageCode);

                int networkMessageLength = buffer.getInt(bufferIndex + 4);
                LOG.info("networkMessageLength: " + networkMessageLength);

                if (networkMessageLength > 0) {

                    String jsonString_networkMessage = buffer.getString(bufferIndex + HEADER_OFFSET, networkMessageLength + HEADER_OFFSET);
                    LOG.info("jsonString_networkMessage: " + jsonString_networkMessage);

                    if (networkMessageCode == NetworkMessageType.CONNECTION_STATE_CHANGE.getCode()) {
                        handleConnectionStateChangeMessage(jsonString_networkMessage);
                    } else if (networkMessageCode == NetworkMessageType.MESSAGE.getCode()) {

//                        SYSTEM_EVENT_BUS.post(new EventMessage(json_data));
                    }

                }

                bufferIndex += networkMessageLength + HEADER_OFFSET;
            }

        } catch (Exception ex) {
            LOG.error("Client:- Something goes wrong parsing the server response...\nClient:-" + ex.getMessage());
        }
    }

    private void handleConnectionStateChangeMessage(String jsonString_networkMessage) {
        ConnectionsUpdatePOJO connectionsState = new Gson().fromJson(jsonString_networkMessage, ConnectionsUpdatePOJO.class);
        EventBus.getDefault().postSticky(new EventConnectionsUpdate(connectionsState));
    }

}
