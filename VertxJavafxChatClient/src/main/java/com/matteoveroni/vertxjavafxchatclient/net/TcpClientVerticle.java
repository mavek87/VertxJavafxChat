package com.matteoveroni.vertxjavafxchatclient.net;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.matteoveroni.vertxjavafxchatbusinesslogic.CommunicationCode;
import com.matteoveroni.vertxjavafxchatbusinesslogic.events.EventClientsConnectedUpdate;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ConnectionsState;
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
                    readSocketBuffer(buffer);
                });

                vertxEventBus.consumer(EventMessage.BUS_ADDRESS, message -> {
                    socket.write(Buffer.buffer().appendString(message.body().toString()));
                });

                vertxEventBus.consumer(EventShutdown.BUS_ADDRESS, message -> {
                    LOG.info("GUI closed.");
                    socket.write(Buffer.buffer().appendString("Ehi server, sto morendo cancellami!"));
                    vertx.close();
                });

            } else {
                LOG.info("Client:- Failed to connect: " + connection.cause().getMessage());
            }
        });
    }

    private void readSocketBuffer(Buffer buffer) {
        final byte HEADER_OFFSET = 8;

        byte buffer_index = 0;

        try {

            LOG.info("buffer.length(): " + buffer.length());
            while (buffer_index < buffer.length()) {

                int communicationCode = buffer.getInt(buffer_index);
                LOG.info("communicationCode: " + communicationCode);

                int messageLength = buffer.getInt(buffer_index + 4);
                LOG.info("messageLength: " + messageLength);

                if (messageLength > 0) {

                    String json_data = buffer.getString(buffer_index + HEADER_OFFSET, messageLength + HEADER_OFFSET);
                    LOG.info("json_data: " + json_data);

                    if (communicationCode == CommunicationCode.CONNECTION_STATE_CHANGE.getCode()) {

                        ConnectionsState connectionsState = new Gson().fromJson(json_data, ConnectionsState.class);
                        SYSTEM_EVENT_BUS.post(new EventClientsConnectedUpdate(connectionsState.getConnectedClients()));

                    } else if (communicationCode == CommunicationCode.MESSAGE.getCode()) {

//                        SYSTEM_EVENT_BUS.post(new EventMessage(json_data));
                    }

                }

                buffer_index += messageLength + HEADER_OFFSET;
            }

        } catch (JsonSyntaxException ex) {
            LOG.error("Something goes wrong parsing the server response...");
        }

    }

    @Subscribe
    public void onEvent(EventMessage event) {
        EventMessage message = (EventMessage) event;
        vertx.eventBus().publish(EventMessage.BUS_ADDRESS, message.getText());
    }

    @Subscribe
    public void onEvent(EventShutdown event) {
        vertx.eventBus().publish(EventShutdown.BUS_ADDRESS, null);
    }
}
