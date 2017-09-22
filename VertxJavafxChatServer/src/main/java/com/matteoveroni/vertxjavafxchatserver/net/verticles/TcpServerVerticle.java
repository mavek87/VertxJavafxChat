package com.matteoveroni.vertxjavafxchatserver.net.verticles;

import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ChatBroadcastMessagePOJO;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ChatPrivateMessagePOJO;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client.ClientConnectionMessage;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client.ClientDisconnectionMessage;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.server.ServerMessageType;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client.ClientPOJO;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.server.ServerConnectionsUpdateMessage;
import com.matteoveroni.vertxjavafxchatserver.net.parser.ClientMessageParser;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpServerVerticle extends AbstractVerticle {

    private final String serverAddress;
    private final int serverPort;

    private static final Logger LOG = LoggerFactory.getLogger(TcpServerVerticle.class);

    private static final Map<ClientPOJO, NetSocket> CONNECTIONS = new ConcurrentHashMap<>();

    private final ClientMessageParser clientMessageParser = new ClientMessageParser();

    public TcpServerVerticle(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        vertx.createNetServer().connectHandler(socket -> {

            socket.handler(buffer -> {

                try {
                    Object clientMessage = clientMessageParser.parse(buffer);

                    if (clientMessage instanceof ClientConnectionMessage) {

                        ClientPOJO connectedClient = ((ClientConnectionMessage) clientMessage).getConnectedClient();
                        handleClientConnection(connectedClient, socket);

                    } else if (clientMessage instanceof ClientDisconnectionMessage) {

                        ClientPOJO disconnectedClient = ((ClientDisconnectionMessage) clientMessage).getDisconnectedClient();
                        handleClientDisconnection(disconnectedClient);

                    } else if (clientMessage instanceof ChatPrivateMessagePOJO) {

                        ChatPrivateMessagePOJO chatPrivateMessage = (ChatPrivateMessagePOJO) clientMessage;
                        handleSendChatPrivateMessage(chatPrivateMessage);

                    } else if (clientMessage instanceof ChatBroadcastMessagePOJO) {

                        ChatBroadcastMessagePOJO chatBroadcastMessage = (ChatBroadcastMessagePOJO) clientMessage;
                        handleSendChatBroadcastMessage(chatBroadcastMessage);

                    }

                } catch (Exception ex) {
                    LOG.error("Something goes wrong parsing a client message..." + ex.getMessage());
                }

//                LOG.info("Socket write handler id: " + socket.writeHandlerID());
            });

        }).listen(serverPort, serverAddress, res -> {

            if (res.succeeded()) {
                LOG.info("I\'m now listening at address: " + serverAddress + " on port: " + serverPort);
                startFuture.complete();
            } else {
                LOG.error("Failed to bind!");
                startFuture.fail(res.cause());
            }

        });
    }

    private void handleClientConnection(ClientPOJO connectedClient, NetSocket socket) {
        LOG.info("New client connection enstablished!");
        CONNECTIONS.put(connectedClient, socket);

        printAllClientsConnectedToServerConsole();
        sendRefreshedServerConnectionsToClients();
    }

    private void printAllClientsConnectedToServerConsole() {
        LOG.info("All the connected clients are:");

        for (ClientPOJO client : CONNECTIONS.keySet()) {
            LOG.info(client.toString());
        }
    }

    private void sendMessageToClient(NetSocket socket, int messageType, String jsonifiedMessage) {
        if (socket != null) {
            socket.write(Buffer.buffer()
                .appendInt(messageType)
                .appendString(jsonifiedMessage)
            );
        }
    }

    private void sendRefreshedServerConnectionsToClients() {
        ServerConnectionsUpdateMessage connectionsUpdateMessage = new ServerConnectionsUpdateMessage(CONNECTIONS.keySet());

        JsonObject json_connectedClients = JsonObject.mapFrom(connectionsUpdateMessage);
        String jsonString_connectedClients = (json_connectedClients.toString());

        for (NetSocket socket : CONNECTIONS.values()) {
            sendMessageToClient(socket, ServerMessageType.CONNECTION_STATE_CHANGE.getCode(), jsonString_connectedClients);
        }
    }

    private void handleClientDisconnection(ClientPOJO disconnectedClient) {
        if (CONNECTIONS.remove(disconnectedClient) != null) {
            sendRefreshedServerConnectionsToClients();
        }
    }

    private void handleSendChatPrivateMessage(ChatPrivateMessagePOJO chatPrivateMessage) {
        JsonObject json_chatPrivateMessage = JsonObject.mapFrom(chatPrivateMessage);
        String jsonString_chatPrivateMessage = (json_chatPrivateMessage.toString());

        NetSocket socket = CONNECTIONS.get(chatPrivateMessage.getTargetClient());
        sendMessageToClient(socket, ServerMessageType.SERVER_CHAT_PRIVATE_MESSAGE.getCode(), jsonString_chatPrivateMessage);
    }

    private void handleSendChatBroadcastMessage(ChatBroadcastMessagePOJO chatBroadcastMessage) {
        JsonObject json_chatBroadcastMessage = JsonObject.mapFrom(chatBroadcastMessage);
        String jsonString_chatBroadcastMessage = (json_chatBroadcastMessage.toString());

        for (ClientPOJO client : CONNECTIONS.keySet()) {
            NetSocket clientSocket = CONNECTIONS.get(client);
            sendMessageToClient(clientSocket, ServerMessageType.SERVER_CHAT_BROADCAST_MESSAGE.getCode(), jsonString_chatBroadcastMessage);
        }
    }
}
