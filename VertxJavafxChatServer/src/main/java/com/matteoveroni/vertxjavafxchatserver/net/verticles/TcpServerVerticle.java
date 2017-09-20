package com.matteoveroni.vertxjavafxchatserver.net.verticles;

import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ChatPrivateMessagePOJO;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.server.ServerMessageType;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client.ClientPOJO;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.server.ServerConnectionsUpdateMessage;
import com.matteoveroni.vertxjavafxchatserver.net.parser.ClientMessageParser;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpServerVerticle extends AbstractVerticle {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8080;

    private static final Logger LOG = LoggerFactory.getLogger(TcpServerVerticle.class);

    private static final Map<ClientPOJO, NetSocket> CONNECTIONS = new ConcurrentHashMap<>();

    private final ClientMessageParser clientMessageParser = new ClientMessageParser();

    @Override
    public void start() throws Exception {

        vertx.createNetServer().connectHandler(socket -> {

            handleClientConnection(socket);

            socket.handler(buffer -> {

                try {
                    Object clientMessage = clientMessageParser.parse(buffer);

                    if (clientMessage instanceof ServerConnectionsUpdateMessage) {

                        ClientPOJO disconnectedClient = (ClientPOJO) clientMessage;
                        handleClientDisconnection(disconnectedClient);

                    } else if (clientMessage instanceof ChatPrivateMessagePOJO) {

                        ChatPrivateMessagePOJO chatPrivateMessage = (ChatPrivateMessagePOJO) clientMessage;
                        handleSendChatPrivateMessage(chatPrivateMessage);

                    }

                } catch (Exception ex) {
                    LOG.error("Something goes wrong parsing a client message..." + ex.getMessage());
                }

//                LOG.info("Socket write handler id: " + socket.writeHandlerID());
            });

        }).listen(SERVER_PORT, SERVER_ADDRESS, res -> {

            if (res.succeeded()) {
                LOG.info("I\'m now listening!");
            } else {
                LOG.error("Failed to bind!");
            }

        });
    }

    private void handleClientConnection(NetSocket socket) {
        LOG.info("New client connection enstablished!");

        saveNewClientConnectedData(socket);
        printAllClientsConnectedToServerConsole();
        sendRefreshedServerConnectionsToClients();
    }

    private void handleClientDisconnection(ClientPOJO disconnectedClient) {
        if (CONNECTIONS.remove(disconnectedClient) != null) {
            sendRefreshedServerConnectionsToClients();
        }
    }

    private void saveNewClientConnectedData(NetSocket socket) {
        ClientPOJO client = new ClientPOJO(socket.remoteAddress().host(), socket.remoteAddress().port());
        CONNECTIONS.put(client, socket);
    }

    private void printAllClientsConnectedToServerConsole() {
        LOG.info("Connected clients now are:");

        for (ClientPOJO client : CONNECTIONS.keySet()) {
            LOG.info(client.toString());
        }
    }

    private void sendRefreshedServerConnectionsToClients() {
        ServerConnectionsUpdateMessage connectionsUpdate = new ServerConnectionsUpdateMessage(CONNECTIONS.keySet());

        JsonObject json_connectedClients = JsonObject.mapFrom(connectionsUpdate);
        String jsonString_connectedClients = (json_connectedClients.toString());

        for (NetSocket socket : CONNECTIONS.values()) {
            socket.write(Buffer.buffer()
                .appendInt(ServerMessageType.CONNECTION_STATE_CHANGE.getCode())
                .appendString(jsonString_connectedClients)
            );
        }
    }

    private void handleSendChatPrivateMessage(ChatPrivateMessagePOJO chatPrivateMessage) {
        JsonObject json_chatPrivateMessage = JsonObject.mapFrom(chatPrivateMessage);
        String jsonString_chatPrivateMessage = (json_chatPrivateMessage.toString());
        
        System.out.println("jsonString_chatPrivateMessage: " + jsonString_chatPrivateMessage);

        NetSocket socket = CONNECTIONS.get(chatPrivateMessage.getTargetClient());

        if (socket != null) {
            socket.write(Buffer.buffer()
                .appendInt(ServerMessageType.SERVER_CHAT_PRIVATE_MESSAGE.getCode())
                .appendString(jsonString_chatPrivateMessage)
            );
        }
    }
}
