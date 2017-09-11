package com.matteoveroni.vertxjavafxchatserver.verticles;

import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ClientPOJO;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TcpServerVerticle extends AbstractVerticle {

    private static final String TCP_SERVER_ADDRESS = "localhost";
    private static final int TCP_SERVER_PORT = 8080;

    private static final List<ClientPOJO> CONNECTED_CLIENTS = new ArrayList<>();

    @Override
    public void start() throws Exception {
        NetServer server = vertx.createNetServer();

        server.connectHandler(socket -> {
            System.out.println("Server:- New client connection enstablished");

            handeNewClientConnection(socket);

            socket.handler(buffer -> {
                System.out.println("Server:- I received some bytes: " + buffer.length());
                System.out.println("Server:- The content is: " + buffer.getString(0, buffer.length()));
            });
            sendFirstGreetingToClient(socket);
        });

        server.listen(TCP_SERVER_PORT, TCP_SERVER_ADDRESS, res -> {
            if (res.succeeded()) {
                System.out.println("Server:- I\'m now listening!");
            } else {
                System.out.println("Server:- Failed to bind!");
            }
        });
    }

    private void handeNewClientConnection(NetSocket socket) {
        saveNewConnectedClient(socket);

        printAllConnectedClients(CONNECTED_CLIENTS);

        Buffer buffer = Buffer.buffer();
        buffer.appendInt(0);

//        JsonObject json_connectedClients = new JsonObject(Json.encode(CONNECTED_CLIENTS));
        CONNECTED_CLIENTS.forEach(client -> {
//            socket.write(json_connectedClients.toBuffer());
            
            String str_client = client.toString();
            socket.write(buffer.appendInt(str_client.length()).appendString(str_client));
        });
    }

    private void saveNewConnectedClient(NetSocket socket) {
        ClientPOJO newConnectedClient = new ClientPOJO(socket.remoteAddress().host(), socket.remoteAddress().port());
        CONNECTED_CLIENTS.add(newConnectedClient);
    }

    private void printAllConnectedClients(Collection<ClientPOJO> clients) {
        clients.forEach((client) -> {
            System.out.println(client);
        });
    }

    private void sendFirstGreetingToClient(NetSocket socket) {
        Buffer buffer = Buffer.buffer().appendString(
                "Hello World by Server (" + socket.localAddress() + ") to " + socket.remoteAddress() + "!"
        );
        socket.write(buffer);
        System.out.println("Server:- Greeting sent!");
    }
}
