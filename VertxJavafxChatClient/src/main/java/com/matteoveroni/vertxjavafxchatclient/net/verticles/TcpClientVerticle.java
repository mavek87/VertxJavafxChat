package com.matteoveroni.vertxjavafxchatclient.net.verticles;

import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ChatBroadcastMessagePOJO;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client.ClientMessageType;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client.ClientPOJO;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.server.ServerConnectionsUpdateMessage;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ChatPrivateMessagePOJO;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client.ClientConnectionMessage;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client.ClientDisconnectionMessage;
import com.matteoveroni.vertxjavafxchatclient.events.EventReceivedConnectionsUpdateMessage;
import com.matteoveroni.vertxjavafxchatclient.events.EventSendChatPrivateMessage;
import com.matteoveroni.vertxjavafxchatclient.events.EventReceivedChatPrivateMessage;
import com.matteoveroni.vertxjavafxchatclient.events.EventClientShutdown;
import com.matteoveroni.vertxjavafxchatclient.events.EventReceivedChatBroadcastMessage;
import com.matteoveroni.vertxjavafxchatclient.events.EventSendChatBroadcastMessage;
import com.matteoveroni.vertxjavafxchatclient.net.parser.ServerMessagesParser;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpClientVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(TcpClientVerticle.class);
    private static final org.greenrobot.eventbus.EventBus SYSTEM_EVENT_BUS = org.greenrobot.eventbus.EventBus.getDefault();

    public static final String SERVER_CONNECTION_CLOSED_EVENT_ADDRESS = "srv_conn_close_evt_address";

    public static String CLIENT_ADDRESS;
    public static Integer CLIENT_PORT;

    private final ServerMessagesParser serverMessagesParser = new ServerMessagesParser();

    public final String serverAddress;
    public final Integer serverPort;
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

                sendConnectionMessageToServer(socket);

                socket.handler((Buffer buffer) -> {

                    try {
                        Object serverMessage = serverMessagesParser.parse(buffer);

                        if (serverMessage instanceof ServerConnectionsUpdateMessage) {

                            SYSTEM_EVENT_BUS.post(new EventReceivedConnectionsUpdateMessage((ServerConnectionsUpdateMessage) serverMessage));

                        } else if (serverMessage instanceof ChatPrivateMessagePOJO) {

                            SYSTEM_EVENT_BUS.post(new EventReceivedChatPrivateMessage((ChatPrivateMessagePOJO) serverMessage));

                        } else if (serverMessage instanceof ChatBroadcastMessagePOJO) {

                            SYSTEM_EVENT_BUS.post(new EventReceivedChatBroadcastMessage((ChatBroadcastMessagePOJO) serverMessage));

                        }

                    } catch (Exception ex) {
                        LOG.error("Something goes wrong parsing a server message... - " + ex.getMessage());
                    }
                });

                socket.endHandler((Void e) -> {
                    vertxEventBus.publish(SERVER_CONNECTION_CLOSED_EVENT_ADDRESS, null);
                });

                vertxEventBus.consumer(EventSendChatPrivateMessage.BUS_ADDRESS, message -> {
                    sendPrivateMessageToOtherClientViaServer(socket, (JsonObject) message.body());
                });

                vertxEventBus.consumer(EventSendChatBroadcastMessage.BUS_ADDRESS, message -> {
                    sendBroadcastMessageToOtherClientViaServer(socket, (JsonObject) message.body());
                });

                vertxEventBus.consumer(EventClientShutdown.BUS_ADDRESS, message -> {
                    LOG.info("Client is going to shutdown..");

                    sendDisconnectionMessageToServer(socket);

                    vertx.close();
                });

            } else {
                startFuture.fail(connection.cause());
            }
        });
    }

    private void sendTCPMessageToServer(NetSocket socket, int messageType, JsonObject json_message) {
        socket.write(Buffer.buffer()
            .appendInt(messageType)
            .appendString(Json.encode(json_message))
        );
    }

    private void sendConnectionMessageToServer(NetSocket socket) {
        ClientPOJO connectingClient = new ClientPOJO(nickname, CLIENT_ADDRESS, CLIENT_PORT);
        ClientConnectionMessage clientConnectionMessage = new ClientConnectionMessage(connectingClient);
        JsonObject json_clientConnectionMessage = JsonObject.mapFrom(clientConnectionMessage);
        sendTCPMessageToServer(socket, ClientMessageType.CLIENT_CONNECTION.getCode(), json_clientConnectionMessage);
    }

    private void sendDisconnectionMessageToServer(NetSocket socket) {
        ClientPOJO disconnectingClient = new ClientPOJO(nickname, socket.localAddress().host(), socket.localAddress().port());
        ClientDisconnectionMessage clientDisconnectionMessage = new ClientDisconnectionMessage(disconnectingClient);
        JsonObject json_clientDisconnectionMessage = JsonObject.mapFrom(clientDisconnectionMessage);
        sendTCPMessageToServer(socket, ClientMessageType.CLIENT_DISCONNECTION.getCode(), json_clientDisconnectionMessage);
    }

    private void sendPrivateMessageToOtherClientViaServer(NetSocket socket, JsonObject json_message) {
        sendTCPMessageToServer(socket, ClientMessageType.CLIENT_CHAT_PRIVATE_MESSAGE.getCode(), json_message);
    }

    private void sendBroadcastMessageToOtherClientViaServer(NetSocket socket, JsonObject json_message) {
        sendTCPMessageToServer(socket, ClientMessageType.CLIENT_CHAT_BROADCAST_MESSAGE.getCode(), json_message);
    }
}
