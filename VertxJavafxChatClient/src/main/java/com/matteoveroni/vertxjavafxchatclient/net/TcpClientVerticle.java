package com.matteoveroni.vertxjavafxchatclient.net;

import com.google.gson.Gson;
import com.matteoveroni.vertxjavafxchatbusinesslogic.NetworkMessageType;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ConnectionsUpdatePOJO;
import com.matteoveroni.vertxjavafxchatclient.events.EventConnectionsUpdate;
import com.matteoveroni.vertxjavafxchatclient.events.EventMessage;
import com.matteoveroni.vertxjavafxchatclient.events.EventShutdown;
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

    private final org.greenrobot.eventbus.EventBus SYSTEM_EVENT_BUS = org.greenrobot.eventbus.EventBus.getDefault();

    @Subscribe
    public void onEvent(EventMessage evt_message) {
        vertx.eventBus().publish(EventMessage.BUS_ADDRESS, evt_message.getText());
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
                LOG.info("Client:- Connected!");

                NetSocket socket = connection.result();
                socket.handler((Buffer buffer) -> {
                    readServerMessages(buffer);
                });

                LOG.info("Client:- socket write handler id: " + socket.writeHandlerID());

                vertxEventBus.consumer(EventMessage.BUS_ADDRESS, message -> {
                    socket.write(Buffer.buffer().appendString(message.body().toString()));
                });

                vertxEventBus.consumer(EventShutdown.BUS_ADDRESS, message -> {
                    LOG.info("Client:- GUI says to be closed");

                    String imDyingName = socket.localAddress().host() + ":" + socket.localAddress().port();

                    socket.write(Buffer.buffer().appendString(imDyingName));
                    vertx.close();
                });

            } else {
                LOG.info("Client:- Failed to connect: " + connection.cause().getMessage());
            }
        });
    }

    private void readServerMessages(Buffer buffer) {
        final int HEADER_OFFSET = 8;

        int bufferIndex = 0;

        try {

            LOG.info("buffer.length(): " + buffer.length());
            while (bufferIndex < buffer.length()) {

                int networkMessageCode = buffer.getInt(bufferIndex);
                LOG.info("communicationCode: " + networkMessageCode);

                int messageLength = buffer.getInt(bufferIndex + 4);
                LOG.info("messageLength: " + messageLength);

                if (messageLength > 0) {

                    String json_message = buffer.getString(bufferIndex + HEADER_OFFSET, messageLength + HEADER_OFFSET);
                    LOG.info("json_data: " + json_message);

                    if (networkMessageCode == NetworkMessageType.CONNECTION_STATE_CHANGE.getCode()) {

                        ConnectionsUpdatePOJO connectionsState = new Gson().fromJson(json_message, ConnectionsUpdatePOJO.class);
                        SYSTEM_EVENT_BUS.postSticky(new EventConnectionsUpdate(connectionsState));

                    } else if (networkMessageCode == NetworkMessageType.MESSAGE.getCode()) {

//                        SYSTEM_EVENT_BUS.post(new EventMessage(json_data));
                    }

                }

                bufferIndex += messageLength + HEADER_OFFSET;
            }

        } catch (Exception ex) {
            LOG.error("Client:- Something goes wrong parsing the server response...\nClient:-" + ex.getMessage());
        }

    }
}
