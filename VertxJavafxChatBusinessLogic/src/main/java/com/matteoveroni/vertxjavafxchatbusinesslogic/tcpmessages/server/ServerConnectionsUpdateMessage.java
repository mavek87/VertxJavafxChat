package com.matteoveroni.vertxjavafxchatbusinesslogic.tcpmessages.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ClientPOJO;
import java.util.Collection;

public class ServerConnectionsUpdateMessage {

    public static final String BUS_ADDRESS = "server_connections_update_msg";

    private final Collection<ClientPOJO> clientsConnected;

    public ServerConnectionsUpdateMessage(@JsonProperty("clientsConnected") Collection<ClientPOJO> clientsConnected) {
        this.clientsConnected = clientsConnected;
    }

    public Collection<ClientPOJO> getClientsConnected() {
        return clientsConnected;
    }
}
