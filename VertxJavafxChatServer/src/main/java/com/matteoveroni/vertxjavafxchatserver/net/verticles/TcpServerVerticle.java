package com.matteoveroni.vertxjavafxchatserver.net.verticles;

import com.matteoveroni.vertxjavafxchatbusinesslogic.network.messages.ChatBroadcastMessage;
import com.matteoveroni.vertxjavafxchatbusinesslogic.network.messages.ChatPrivateMessage;
import com.matteoveroni.vertxjavafxchatbusinesslogic.network.messages.ConnectedHostsUpdateMessage;
import com.matteoveroni.vertxjavafxchatbusinesslogic.network.messages.ConnectionMessage;
import com.matteoveroni.vertxjavafxchatbusinesslogic.network.messages.DisconnectionMessage;
import com.matteoveroni.vertxjavafxchatbusinesslogic.network.messages.NetworkMessage;
import static com.matteoveroni.vertxjavafxchatbusinesslogic.network.messages.NetworkMessageType.CONNECTED_HOSTS_UPDATE;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ClientPOJO;
import com.matteoveroni.vertxjavafxchatserver.events.EventUpdateNumberOfConnectedHosts;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetSocket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.naming.OperationNotSupportedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpServerVerticle extends AbstractVerticle {

    private final String serverAddress;
    private final int serverPort;

    private static final Logger LOG = LoggerFactory.getLogger(TcpServerVerticle.class);

    private final Map<ClientPOJO, NetSocket> CONNECTIONS = new ConcurrentHashMap<>();

    public TcpServerVerticle(String serverAddress, int serverPort) {
        CONNECTIONS.clear();
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertx.createNetServer().connectHandler(socket -> {
            socket.handler(buffer -> {
                try {
                    String jsonString_NetworkMessage = buffer.getString(0, buffer.length());
                    LOG.info("ClientMessage: " + jsonString_NetworkMessage);

                    JsonObject json_networkMessage = new JsonObject(jsonString_NetworkMessage);
                    NetworkMessage networkMessage = json_networkMessage.mapTo(NetworkMessage.class);

                    switch (networkMessage.getMessageType()) {
                        case CONNECTED_HOSTS_UPDATE:
                            throw new OperationNotSupportedException(CONNECTED_HOSTS_UPDATE + " - network message not handled by the server.");
                        case CLIENT_CONNECTION:
                            handleClientConnection(((ConnectionMessage) networkMessage).getConnectedClient(), socket);
                            break;
                        case CLIENT_DISCONNECTION:
                            handleClientDisconnection(((DisconnectionMessage) networkMessage).getDisconnectedClient());
                            break;
                        case CHAT_BROADCAST_MESSAGE:
                            handleSendChatBroadcastMessage((ChatBroadcastMessage) networkMessage);
                            break;
                        case CHAT_PRIVATE_MESSAGE:
                            handleSendChatPrivateMessage((ChatPrivateMessage) networkMessage);
                            break;
                    }
                } catch (Exception ex) {
                    LOG.error("Something goes wrong parsing a network message..." + ex.getMessage());
                }
            });
        }).listen(serverPort, serverAddress, deploy -> {
            if (deploy.succeeded()) {
                LOG.info("Server listening at address: " + serverAddress + " on port: " + serverPort);
                startFuture.complete();
            } else {
                LOG.error("Failed to bind!");
                startFuture.fail(deploy.cause());
            }

        });
    }

    private void sendNetworkMessageToClient(NetSocket socket, JsonObject json_networkMessage) {
        if (socket != null) {
            socket.write(Buffer.buffer().appendString(Json.encode(json_networkMessage)));
        }
    }

    private void handleSendChatPrivateMessage(ChatPrivateMessage chatPrivateMessage) {
        JsonObject json_chatPrivateMessage = JsonObject.mapFrom(chatPrivateMessage);

        NetSocket socket = CONNECTIONS.get(chatPrivateMessage.getTargetClient());
        sendNetworkMessageToClient(socket, json_chatPrivateMessage);
    }

    private void handleSendChatBroadcastMessage(ChatBroadcastMessage chatBroadcastMessage) {
        JsonObject json_chatBroadcastMessage = JsonObject.mapFrom(chatBroadcastMessage);

        for (ClientPOJO client : CONNECTIONS.keySet()) {
            NetSocket socket = CONNECTIONS.get(client);
            sendNetworkMessageToClient(socket, json_chatBroadcastMessage);
        }
    }

    private void handleClientConnection(ClientPOJO connectedClient, NetSocket socket) {
        LOG.info("New client connection enstablished!");
        CONNECTIONS.put(connectedClient, socket);

        printAllConnectedClientsDataToConsole();
        sendConnectedHostsUpdateNetworkMessageToClients();
        sendEventUpdateNumberOfConnectedHosts();
    }

    private void handleClientDisconnection(ClientPOJO disconnectedClient) {
        if (CONNECTIONS.remove(disconnectedClient) != null) {
            sendConnectedHostsUpdateNetworkMessageToClients();
        }
        sendEventUpdateNumberOfConnectedHosts();
    }

    private void printAllConnectedClientsDataToConsole() {
        LOG.info("All the connected clients are:");

        for (ClientPOJO client : CONNECTIONS.keySet()) {
            LOG.info(client.toString());
        }
    }

    private void sendConnectedHostsUpdateNetworkMessageToClients() {
        ConnectedHostsUpdateMessage connectedHostsUpdateMessage = new ConnectedHostsUpdateMessage(CONNECTIONS.keySet());
        JsonObject json_connectedHostsUpdateMessage = JsonObject.mapFrom(connectedHostsUpdateMessage);

        for (NetSocket socket : CONNECTIONS.values()) {
            sendNetworkMessageToClient(socket, json_connectedHostsUpdateMessage);
        }
    }

    private void sendEventUpdateNumberOfConnectedHosts() {
        JsonObject json_eventUpdate = JsonObject.mapFrom(new EventUpdateNumberOfConnectedHosts(CONNECTIONS.size()));
        vertx.eventBus().publish(EventUpdateNumberOfConnectedHosts.BUS_ADDRESS, json_eventUpdate);
    }

}
