package com.matteoveroni.vertxjavafxchatclient.net.verticles;

import com.google.gson.Gson;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client.ClientMessageType;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client.ClientPOJO;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.server.ServerConnectionsUpdate;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.server.ServerMessage;
import com.matteoveroni.vertxjavafxchatclient.events.EventConnectionsUpdate;
import com.matteoveroni.vertxjavafxchatclient.events.EventChatMessage;
import com.matteoveroni.vertxjavafxchatclient.events.EventShutdown;
import com.matteoveroni.vertxjavafxchatclient.net.parser.ServerMessageParser;
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

    private static final String TCP_SERVER_ADDRESS = "localhost";
    private static final int TCP_SERVER_PORT = 8080;

    private static final Gson GSON = new Gson();

    private final ServerMessageParser serverMessageParser = new ServerMessageParser();

    private final org.greenrobot.eventbus.EventBus SYSTEM_EVENT_BUS = org.greenrobot.eventbus.EventBus.getDefault();

    @Subscribe
    public void onEvent(EventChatMessage evt_message) {
        vertx.eventBus().publish(EventChatMessage.BUS_ADDRESS, evt_message.getText());
    }

    @Subscribe
    public void onEvent(EventShutdown evt_shutdown) {
        vertx.eventBus().publish(EventShutdown.BUS_ADDRESS, null);
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
                socket.handler((Buffer buffer) -> {
                    try {
                        ServerMessage serverMessage = serverMessageParser.parse(buffer);
                        switch (serverMessage.getMessageType()) {
                            case CONNECTION_STATE_CHANGE:
                                SYSTEM_EVENT_BUS.post(new EventConnectionsUpdate((ServerConnectionsUpdate) serverMessage.getMessage()));
                                break;
                            case CHAT_MESSAGE:
                                SYSTEM_EVENT_BUS.post(new EventChatMessage((String) serverMessage.getMessage()));
                                break;
                        }
                    } catch (Exception ex) {
                        LOG.error("Something goes wrong parsing a server message... - " + ex.getMessage());
                    }
                });

                LOG.info("Socket write handler id: " + socket.writeHandlerID());

                vertxEventBus.consumer(EventChatMessage.BUS_ADDRESS, message -> {
                    socket.write(Buffer.buffer().appendString(message.body().toString()));
                });

                vertxEventBus.consumer(EventShutdown.BUS_ADDRESS, message -> {
                    LOG.info("Client GUI it\'s been closed. Tcp client is going to be shutdown too");

                    ClientPOJO disconnectingClient = new ClientPOJO(socket.localAddress().host(), socket.localAddress().port());

                    socket.write(
                        Buffer.buffer()
                        .appendInt(ClientMessageType.CLIENT_DISCONNECTION.getCode())
                        .appendString(GSON.toJson(disconnectingClient, ClientPOJO.class))
                    );

                    vertx.close();
                });

            } else {
                LOG.info("Failed to connect: " + connection.cause().getMessage());
            }
        });
    }
    
}
