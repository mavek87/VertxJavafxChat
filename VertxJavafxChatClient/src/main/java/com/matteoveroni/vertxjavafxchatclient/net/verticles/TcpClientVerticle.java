package com.matteoveroni.vertxjavafxchatclient.net.verticles;

import com.google.gson.Gson;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client.ClientMessageType;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client.ClientPOJO;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.server.ServerConnectionsUpdateMessage;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ChatPrivateMessagePOJO;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client.ClientDisconnectionMessage;
import com.matteoveroni.vertxjavafxchatclient.events.EventReceivedConnectionsUpdateMessage;
import com.matteoveroni.vertxjavafxchatclient.events.EventSendChatMessage;
import com.matteoveroni.vertxjavafxchatclient.events.EventReceivedChatPrivateMessage;
import com.matteoveroni.vertxjavafxchatclient.events.EventClientShutdown;
import com.matteoveroni.vertxjavafxchatclient.net.parser.ServerMessagesParser;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import org.greenrobot.eventbus.Subscribe;
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

    @Subscribe
    public void onGUIEvent(EventSendChatMessage event) {
        ChatPrivateMessagePOJO chatPrivateMessage = event.getChatPrivateMessage();
        String jsonString_chatPrivateMessage = GSON.toJson(chatPrivateMessage, ChatPrivateMessagePOJO.class);
        vertx.eventBus().publish(EventSendChatMessage.BUS_ADDRESS, jsonString_chatPrivateMessage);
    }

    @Subscribe
    public void onGUIEvent(EventClientShutdown evt_shutdown) {
        vertx.eventBus().publish(EventClientShutdown.BUS_ADDRESS, null);
    }

    @Override
    public void start() throws Exception {
        SYSTEM_EVENT_BUS.register(this);
        EventBus vertxEventBus = vertx.eventBus();

        NetClientOptions options = new NetClientOptions().setConnectTimeout(10000);
        vertx.createNetClient(options).connect(TCP_SERVER_PORT, TCP_SERVER_ADDRESS, (AsyncResult<NetSocket> connection) -> {

            if (connection.succeeded()) {
                LOG.info("Connected to server!");

                NetSocket socket = connection.result();

                CLIENT_ADDRESS = socket.localAddress().host();
                CLIENT_PORT = socket.localAddress().port();

                socket.handler((Buffer buffer) -> {
                    try {
                        Object serverMessage = serverMessagesParser.parse(buffer);

                        if (serverMessage instanceof ServerConnectionsUpdateMessage) {
                            
                            SYSTEM_EVENT_BUS.post(new EventReceivedConnectionsUpdateMessage((ServerConnectionsUpdateMessage) serverMessage));
                       
                        } else if(serverMessage instanceof ChatPrivateMessagePOJO) {
                            ChatPrivateMessagePOJO cpm = (ChatPrivateMessagePOJO) serverMessage;
                            EventReceivedChatPrivateMessage chatPrivateMessage = new EventReceivedChatPrivateMessage(cpm);
                            SYSTEM_EVENT_BUS.post(chatPrivateMessage);
                        
                        }

                    } catch (Exception ex) {
                        LOG.error("Something goes wrong parsing a server message... - " + ex.getMessage());
                    }
                });

//                LOG.info("Socket write handler id: " + socket.writeHandlerID());

                vertxEventBus.consumer(EventSendChatMessage.BUS_ADDRESS, message -> {
                    String jsonString_chatPrivateMessage = (String) message.body();

                    socket.write(Buffer.buffer()
                        .appendInt(ClientMessageType.CLIENT_CHAT_PRIVATE_MESSAGE.getCode())
                        .appendString(jsonString_chatPrivateMessage)
                    );
                });

                vertxEventBus.consumer(EventClientShutdown.BUS_ADDRESS, message -> {
                    LOG.info("Client GUI it\'s been closed. Tcp client is going to be shutdown too");

                    ClientPOJO disconnectingClient = new ClientPOJO(socket.localAddress().host(), socket.localAddress().port());
                    ClientDisconnectionMessage clientDisconnectionMessage = new ClientDisconnectionMessage(disconnectingClient);
                    String jsonString_clientDisconnectionMessage = GSON.toJson(clientDisconnectionMessage, ClientDisconnectionMessage.class);

                    socket.write(Buffer.buffer()
                        .appendInt(ClientMessageType.CLIENT_DISCONNECTION.getCode())
                        .appendString(jsonString_clientDisconnectionMessage)
                    );

                    vertx.close();
                });

            } else {
                LOG.info("Failed to connect: " + connection.cause().getMessage());
            }
        });
    }

}
