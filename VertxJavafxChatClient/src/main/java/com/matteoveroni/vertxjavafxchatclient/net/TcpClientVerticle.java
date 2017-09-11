package com.matteoveroni.vertxjavafxchatclient.net;

import com.matteoveroni.vertxtcpclient.events.EventMessage;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import java.util.Random;
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
        NetClient client = vertx.createNetClient(options);
        client.connect(TCP_SERVER_PORT, TCP_SERVER_ADDRESS, connection -> {
            if (connection.succeeded()) {

                LOG.info("Client:- Connected!");

                NetSocket socket = connection.result();
                socket.handler((Buffer buffer) -> {
//                    JsonObject json_listOfClients = JsonObject.mapFrom(Json.decodeValue(buffer.getString(0, buffer.length()), MessageListOfClients.class));
//                    json_listOfClients.getJsonArray(TCP_SERVER_ADDRESS)
                });

                // Each time the timer send a message write to the server a random message
//                eventBus.consumer(TimerVerticle.BUS_ADDRESS_TIMER, message -> {
//                    socket.write(Buffer.buffer().appendString(getRandomMessage()));
//                });
                vertxEventBus.consumer(EventMessage.BUS_EVENT_MESSAGE_ADDRESS, message -> {
                    socket.write(Buffer.buffer().appendString(message.body().toString()));

                });

            } else {
                LOG.info("Client:- Failed to connect: " + connection.cause().getMessage());
            }
        });
    }

//    public void onEvent(EventMessage message) {
//        vertx.eventBus().publish(EventMessage.BUS_ADDRESS_EVENT, message.getText());
//    }
    private String getRandomMessage() {
        Random randMessage = new Random();
        return Integer.toString(randMessage.nextInt());
    }

    @Subscribe
    public void onEvent(EventMessage event) {
        EventMessage message = (EventMessage) event;
        vertx.eventBus().publish(EventMessage.BUS_EVENT_MESSAGE_ADDRESS, message.getText());
    }
}
