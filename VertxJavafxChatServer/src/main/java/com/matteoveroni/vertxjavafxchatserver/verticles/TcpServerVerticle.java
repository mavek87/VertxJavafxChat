package com.matteoveroni.vertxjavafxchatserver.verticles;

import com.matteoveroni.vertxjavafxchatbusinesslogic.CommunicationCode;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ClientPOJO;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ConnectionsState;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpServerVerticle extends AbstractVerticle {

    private static final String TCP_SERVER_ADDRESS = "localhost";
    private static final int TCP_SERVER_PORT = 8080;

    private static final List<ClientPOJO> CONNECTED_CLIENTS = new ArrayList<>();

    private static final Logger LOG = LoggerFactory.getLogger(TcpServerVerticle.class);

    @Override
    public void start() throws Exception {
        NetServer server = vertx.createNetServer();

        server.connectHandler(socket -> {
            LOG.info("Server:- New client connection enstablished");

            handeNewClientConnection(socket);

            socket.handler(buffer -> {
                LOG.info("Server:- I received some bytes: " + buffer.length());
                LOG.info("Server:- " + buffer.getString(0, buffer.length()));
            });
//            sendFirstGreetingToClient(socket);
        });

        server.listen(TCP_SERVER_PORT, TCP_SERVER_ADDRESS, res -> {
            if (res.succeeded()) {
                LOG.info("Server:- I\'m now listening!");
            } else {
                LOG.error("Server:- Failed to bind!");
            }
        });
    }

    private void handeNewClientConnection(NetSocket socket) {
        saveNewClientConnected(socket);
        printAllConnectedClientsToServerConsole();

        JsonObject json_connectedClients = new JsonObject(Json.encode(new ConnectionsState(CONNECTED_CLIENTS)));
        String str_connectedClients = (json_connectedClients.toString());

        socket.write(Buffer.buffer()
            .appendInt(CommunicationCode.CONNECTION_STATE_CHANGE.getCode())
            .appendInt(str_connectedClients.length())
            .appendString(str_connectedClients)
        );
    }

    private void saveNewClientConnected(NetSocket socket) {
        ClientPOJO newConnectedClient = new ClientPOJO(socket.remoteAddress().host(), socket.remoteAddress().port());
        CONNECTED_CLIENTS.add(newConnectedClient);
    }

    private void printAllConnectedClientsToServerConsole() {
        CONNECTED_CLIENTS.forEach((client) -> {
            LOG.info(client.toString());
        });
    }

//    private void sendFirstGreetingToClient(NetSocket socket) {
//        Buffer buffer = Buffer.buffer()
//            .appendInt(CommunicationCode.MESSAGE.getCode())
//            .appendString(
//                "Hello World by Server (" + socket.localAddress() + ") to " + socket.remoteAddress() + "!"
//            );
//        socket.write(buffer);
//        LOG.info("Server:- Greeting sent!");
//    }
}
