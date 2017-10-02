package com.matteoveroni.vertxjavafxchatclient.network.verticles;

import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ClientPOJO;
import com.matteoveroni.vertxjavafxchatbusinesslogic.network.messages.ChatBroadcastMessage;
import com.matteoveroni.vertxjavafxchatbusinesslogic.network.messages.ChatPrivateMessage;
import com.matteoveroni.vertxjavafxchatbusinesslogic.network.messages.ConnectionMessage;
import com.matteoveroni.vertxjavafxchatbusinesslogic.network.messages.DisconnectionMessage;
import com.matteoveroni.vertxjavafxchatbusinesslogic.network.messages.ConnectedHostsUpdateMessage;
import com.matteoveroni.vertxjavafxchatbusinesslogic.network.messages.NetworkMessage;
import com.matteoveroni.vertxjavafxchatclient.events.EventDestroyClientRequest;
import com.matteoveroni.vertxjavafxchatclient.events.EventCloseGUIRequest;
import com.matteoveroni.vertxjavafxchatclient.events.EventReceivedChatBroadcastMessage;
import com.matteoveroni.vertxjavafxchatclient.events.EventReceivedChatPrivateMessage;
import com.matteoveroni.vertxjavafxchatclient.events.EventSendChatBroadcastMessage;
import com.matteoveroni.vertxjavafxchatclient.events.EventSendChatPrivateMessage;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpClientVerticle extends AbstractVerticle {

    public static final String SOCKET_CLOSED_EVENT_ADDRESS = "socket_closed_evt_address";
    public static final String SOCKET_ERROR_EVENT_ADDRESS = "socket_error_evt_address";

    public static String CLIENT_ADDRESS;
    public static Integer CLIENT_PORT;

    private static final Logger LOG = LoggerFactory.getLogger(TcpClientVerticle.class);

    private final String serverAddress;
    private final Integer serverPort;
    private final String nickname;

    private EventBus vertxEventBus;

    public TcpClientVerticle(String serverAddress, Integer serverPort, String nickname) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.nickname = nickname;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertxEventBus = vertx.eventBus();

        NetClientOptions options = new NetClientOptions().setConnectTimeout(10000);
        vertx.createNetClient(options).connect(serverPort, serverAddress, (AsyncResult<NetSocket> connection) -> {

            if (connection.succeeded()) {

                startFuture.complete();

                LOG.info("Connected to server!");

                NetSocket socket = connection.result();

                CLIENT_ADDRESS = socket.localAddress().host();
                CLIENT_PORT = socket.localAddress().port();

                sendClientConnectionNetworkMessageToServer(socket);

                socket.handler((Buffer buffer) -> {
                    try {

                        String jsonString_networkMessage = buffer.getString(0, buffer.length());
                        LOG.info("ServerMessage: " + jsonString_networkMessage);

                        JsonObject json_networkMessage = new JsonObject(jsonString_networkMessage);
                        NetworkMessage networkMessage = json_networkMessage.mapTo(NetworkMessage.class);

                        switch (networkMessage.getMessageType()) {
                            case CONNECTED_HOSTS_UPDATE:
                                JsonObject json_connectedHostsUpdateMsg = JsonObject.mapFrom((ConnectedHostsUpdateMessage) networkMessage);
                                vertxEventBus.publish(ConnectedHostsUpdateMessage.BUS_ADDRESS, json_connectedHostsUpdateMsg);
                                break;
                            case CLIENT_CONNECTION:

                                break;
                            case CLIENT_DISCONNECTION:

                                break;
                            case CHAT_BROADCAST_MESSAGE:
                                JsonObject json_receivedChatBroadcastMessage = JsonObject.mapFrom((ChatBroadcastMessage) networkMessage);
                                vertxEventBus.publish(EventReceivedChatBroadcastMessage.BUS_ADDRESS, json_receivedChatBroadcastMessage);
                                break;
                            case CHAT_PRIVATE_MESSAGE:
                                JsonObject json_receivedChatPrivateMessage = JsonObject.mapFrom((ChatPrivateMessage) networkMessage);
                                vertxEventBus.publish(EventReceivedChatPrivateMessage.BUS_ADDRESS, json_receivedChatPrivateMessage);
                                break;
                        }

                    } catch (Exception ex) {
                        LOG.error("Something goes wrong trying to parse a message from the server... - " + ex.getMessage());
                    }
                });

                socket.endHandler((Void e) -> {
                    vertxEventBus.publish(SOCKET_CLOSED_EVENT_ADDRESS, null);
                });

                socket.exceptionHandler((Throwable e) -> {
                    vertxEventBus.publish(SOCKET_ERROR_EVENT_ADDRESS, e.getMessage());
                });

                vertxEventBus.consumer(EventSendChatPrivateMessage.BUS_ADDRESS, message -> {
                    sendPrivateMessageToOtherClientViaServer(socket, (JsonObject) message.body());
                });

                vertxEventBus.consumer(EventSendChatBroadcastMessage.BUS_ADDRESS, message -> {
                    sendBroadcastMessageToOtherClientViaServer(socket, (JsonObject) message.body());
                });

                vertxEventBus.consumer(EventCloseGUIRequest.BUS_ADDRESS, message -> {
                    LOG.info("Client send a message to server about his disconnection..");
                    sendClientDisconnectionNetworkMessageToServer(socket);
                    message.reply("Action completed");
                });
            } else {
                startFuture.fail(connection.cause());
            }
        });
    }

    private void sendNetworkMessageToServer(NetSocket socket, JsonObject json_networkMessage) {
        if (socket != null) {
            socket.write(Buffer.buffer()
                    .appendString(Json.encode(json_networkMessage))
            );
        }
    }

    private void sendClientConnectionNetworkMessageToServer(NetSocket socket) {
        ClientPOJO connectingClient = new ClientPOJO(nickname, CLIENT_ADDRESS, CLIENT_PORT);
        ConnectionMessage clientConnectionMessage = new ConnectionMessage(connectingClient);
        JsonObject json_clientConnectionMessage = JsonObject.mapFrom(clientConnectionMessage);
        sendNetworkMessageToServer(socket, json_clientConnectionMessage);
    }

    private void sendClientDisconnectionNetworkMessageToServer(NetSocket socket) {
        ClientPOJO disconnectingClient = new ClientPOJO(nickname, socket.localAddress().host(), socket.localAddress().port());
        DisconnectionMessage clientDisconnectionMessage = new DisconnectionMessage(disconnectingClient);
        JsonObject json_clientDisconnectionMessage = JsonObject.mapFrom(clientDisconnectionMessage);
        sendNetworkMessageToServer(socket, json_clientDisconnectionMessage);
    }

    private void sendPrivateMessageToOtherClientViaServer(NetSocket socket, JsonObject json_message) {
        sendNetworkMessageToServer(socket, json_message);
    }

    private void sendBroadcastMessageToOtherClientViaServer(NetSocket socket, JsonObject json_message) {
        sendNetworkMessageToServer(socket, json_message);
    }
}
