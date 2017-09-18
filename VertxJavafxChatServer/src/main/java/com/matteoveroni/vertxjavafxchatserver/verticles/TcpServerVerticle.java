package com.matteoveroni.vertxjavafxchatserver.verticles;

import com.matteoveroni.vertxjavafxchatbusinesslogic.CommunicationCode;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ClientPOJO;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ConnectionsState;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetSocket;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpServerVerticle extends AbstractVerticle {

    private static final String TCP_SERVER_ADDRESS = "localhost";
    private static final int TCP_SERVER_PORT = 8080;

    private static final Map<ClientPOJO, NetSocket> CONNECTIONS = new HashMap<>();

    private static final Logger LOG = LoggerFactory.getLogger(TcpServerVerticle.class);

    @Override
    public void start() throws Exception {

        vertx.createNetServer().connectHandler(socket -> {

            LOG.info("Server:- New client connection enstablished");

            handleNewClientConnection(socket);

            socket.handler(buffer -> {

                String text = buffer.getString(0, buffer.length());
                LOG.info("Server:- I received " + buffer.length() + " bytes: " + text);

                String[] addressAndPort = text.split(":");
                String address = addressAndPort[0];
                String port = addressAndPort[1];

                for (ClientPOJO client : CONNECTIONS.keySet()) {
                    if (client.getAddress().equals(address) && client.getPort() == Integer.valueOf(port)) {
                        CONNECTIONS.remove(client);
                        sendAllConnectedClientsToClient();
                    }
                }

//                if (text.equals("Ehi server, sto morendo cancellami!")) {
//                    
//                }
            });

//            sendFirstGreetingToClient(socket);
        }).listen(TCP_SERVER_PORT, TCP_SERVER_ADDRESS, res -> {
            if (res.succeeded()) {
                LOG.info("Server:- I\'m now listening!");
            } else {
                LOG.error("Server:- Failed to bind!");
            }
        });
    }

    private void handleNewClientConnection(NetSocket socket) {
        saveNewClientConnected(socket);
        printAllConnectedClientsToServerConsole();
        sendAllConnectedClientsToClient();
    }

    private void sendAllConnectedClientsToClient() {
        JsonObject json_connectedClients = new JsonObject(Json.encode(new ConnectionsState(CONNECTIONS.keySet())));
        String str_connectedClients = (json_connectedClients.toString());

        for (NetSocket openSocket : CONNECTIONS.values()) {
            openSocket.write(Buffer.buffer()
                    .appendInt(CommunicationCode.CONNECTION_STATE_CHANGE.getCode())
                    .appendInt(str_connectedClients.length())
                    .appendString(str_connectedClients)
            );
        }
    }

    private void saveNewClientConnected(NetSocket socket) {
        ClientPOJO newConnectedClient = new ClientPOJO(socket.remoteAddress().host(), socket.remoteAddress().port());

        CONNECTIONS.put(newConnectedClient, socket);
    }

    private void printAllConnectedClientsToServerConsole() {
        for (ClientPOJO client : CONNECTIONS.keySet()) {
            LOG.info(client.toString());
        };
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
