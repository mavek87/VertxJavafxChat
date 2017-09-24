package com.matteoveroni.vertxjavafxchatserver.net.verticles;

import com.matteoveroni.vertxjavafxchatbusinesslogic.tcpmessages.ChatBroadcastMessage;
import com.matteoveroni.vertxjavafxchatbusinesslogic.tcpmessages.ChatPrivateMessage;
import com.matteoveroni.vertxjavafxchatbusinesslogic.tcpmessages.client.ClientConnectionMessage;
import com.matteoveroni.vertxjavafxchatbusinesslogic.tcpmessages.client.ClientDisconnectionMessage;
import com.matteoveroni.vertxjavafxchatbusinesslogic.tcpmessages.server.ServerMessageType;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ClientPOJO;
import com.matteoveroni.vertxjavafxchatbusinesslogic.tcpmessages.server.ServerConnectionsUpdateMessage;
import com.matteoveroni.vertxjavafxchatserver.events.EventNumberOfConnectedHostsUpdate;
import com.matteoveroni.vertxjavafxchatserver.net.parser.ClientMessageParser;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
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
    private static final org.greenrobot.eventbus.EventBus SYSTEM_EVENT_BUS = org.greenrobot.eventbus.EventBus.getDefault();

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

                    } else if (clientMessage instanceof ChatPrivateMessage) {

                        ChatPrivateMessage chatPrivateMessage = (ChatPrivateMessage) clientMessage;
                        handleSendChatPrivateMessage(chatPrivateMessage);

                    } else if (clientMessage instanceof ChatBroadcastMessage) {

                        ChatBroadcastMessage chatBroadcastMessage = (ChatBroadcastMessage) clientMessage;
                        handleSendChatBroadcastMessage(chatBroadcastMessage);

                    }

                } catch (Exception ex) {
                    LOG.error("Something goes wrong parsing a client message..." + ex.getMessage());
                }

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
        SYSTEM_EVENT_BUS.postSticky(new EventNumberOfConnectedHostsUpdate(CONNECTIONS.size()));
    }

    private void printAllClientsConnectedToServerConsole() {
        LOG.info("All the connected clients are:");

        for (ClientPOJO client : CONNECTIONS.keySet()) {
            LOG.info(client.toString());
        }
    }

    private void sendTCPMessageToClient(NetSocket socket, int messageType, JsonObject json_message) {
        if (socket != null) {
            socket.write(Buffer.buffer()
                .appendInt(messageType)
                .appendString(Json.encode(json_message))
            );
        }
    }

    private void sendRefreshedServerConnectionsToClients() {
        ServerConnectionsUpdateMessage connectionsUpdateMessage = new ServerConnectionsUpdateMessage(CONNECTIONS.keySet());

        JsonObject json_connectedClients = JsonObject.mapFrom(connectionsUpdateMessage);

        for (NetSocket socket : CONNECTIONS.values()) {
            sendTCPMessageToClient(socket, ServerMessageType.CONNECTION_STATE_CHANGE.getCode(), json_connectedClients);
        }
    }

    private void handleClientDisconnection(ClientPOJO disconnectedClient) {
        if (CONNECTIONS.remove(disconnectedClient) != null) {
            sendRefreshedServerConnectionsToClients();
        }
        SYSTEM_EVENT_BUS.postSticky(new EventNumberOfConnectedHostsUpdate(CONNECTIONS.size()));
    }

    private void handleSendChatPrivateMessage(ChatPrivateMessage chatPrivateMessage) {
        JsonObject json_chatPrivateMessage = JsonObject.mapFrom(chatPrivateMessage);

        NetSocket socket = CONNECTIONS.get(chatPrivateMessage.getTargetClient());
        sendTCPMessageToClient(socket, ServerMessageType.SERVER_CHAT_PRIVATE_MESSAGE.getCode(), json_chatPrivateMessage);
    }

    private void handleSendChatBroadcastMessage(ChatBroadcastMessage chatBroadcastMessage) {
        JsonObject json_chatBroadcastMessage = JsonObject.mapFrom(chatBroadcastMessage);

        for (ClientPOJO client : CONNECTIONS.keySet()) {
            NetSocket clientSocket = CONNECTIONS.get(client);
            sendTCPMessageToClient(clientSocket, ServerMessageType.SERVER_CHAT_BROADCAST_MESSAGE.getCode(), json_chatBroadcastMessage);
        }
    }
}
