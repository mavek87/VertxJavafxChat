package com.matteoveroni.vertxjavafxchatclient.net.verticles;

import com.google.gson.Gson;
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
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpClientVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(TcpClientVerticle.class);
    private static final Gson GSON = new Gson();
    private static final org.greenrobot.eventbus.EventBus SYSTEM_EVENT_BUS = org.greenrobot.eventbus.EventBus.getDefault();

    public static final String TCP_SERVER_ADDRESS = "localhost";
    public static final int TCP_SERVER_PORT = 8080;

    public static String CLIENT_ADDRESS;
    public static Integer CLIENT_PORT;

    private final ServerMessagesParser serverMessagesParser = new ServerMessagesParser();

    private final String nickname;
    
    private EventBus vertxEventBus;
    
    public TcpClientVerticle(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertxEventBus = vertx.eventBus();

        NetClientOptions options = new NetClientOptions().setConnectTimeout(10000);
        vertx.createNetClient(options).connect(TCP_SERVER_PORT, TCP_SERVER_ADDRESS, (AsyncResult<NetSocket> connection) -> {

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

                vertxEventBus.consumer(EventSendChatPrivateMessage.BUS_ADDRESS, message -> {
                    sendPrivateMessageToOtherClientViaServer(socket, (String) message.body());
                });

                vertxEventBus.consumer(EventSendChatBroadcastMessage.BUS_ADDRESS, message -> {
                    sendBroadcastMessageToOtherClientViaServer(socket, (String) message.body());
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

    private void sendTCPMessageToServer(NetSocket socket, int messageType, String jsonifiedMessage) {
        socket.write(Buffer.buffer()
            .appendInt(messageType)
            .appendString(jsonifiedMessage)
        );
    }

    private void sendConnectionMessageToServer(NetSocket socket) {
        ClientPOJO connectingClient = new ClientPOJO(nickname, CLIENT_ADDRESS, CLIENT_PORT);
        ClientConnectionMessage clientConnectionMessage = new ClientConnectionMessage(connectingClient);
        String jsonString_clientConnectionMessage = GSON.toJson(clientConnectionMessage, ClientConnectionMessage.class);

        sendTCPMessageToServer(socket, ClientMessageType.CLIENT_CONNECTION.getCode(), jsonString_clientConnectionMessage);
    }

    private void sendDisconnectionMessageToServer(NetSocket socket) {
        ClientPOJO disconnectingClient = new ClientPOJO(nickname, socket.localAddress().host(), socket.localAddress().port());
        ClientDisconnectionMessage clientDisconnectionMessage = new ClientDisconnectionMessage(disconnectingClient);
        String jsonString_clientDisconnectionMessage = GSON.toJson(clientDisconnectionMessage, ClientDisconnectionMessage.class);

        sendTCPMessageToServer(socket, ClientMessageType.CLIENT_DISCONNECTION.getCode(), jsonString_clientDisconnectionMessage);
    }

    private void sendPrivateMessageToOtherClientViaServer(NetSocket socket, String jsonString_message) {
        sendTCPMessageToServer(socket, ClientMessageType.CLIENT_CHAT_PRIVATE_MESSAGE.getCode(), jsonString_message);
    }

    private void sendBroadcastMessageToOtherClientViaServer(NetSocket socket, String jsonString_message) {
        sendTCPMessageToServer(socket, ClientMessageType.CLIENT_CHAT_BROADCAST_MESSAGE.getCode(), jsonString_message);
    }
}
