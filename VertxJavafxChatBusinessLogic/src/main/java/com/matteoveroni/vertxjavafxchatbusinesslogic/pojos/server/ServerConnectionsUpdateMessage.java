package com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.server;

import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client.ClientPOJO;
import java.util.Collection;

public class ServerConnectionsUpdateMessage {

    private final Collection<ClientPOJO> clientsConnected;

    public ServerConnectionsUpdateMessage(Collection<ClientPOJO> clientsConnected) {
        this.clientsConnected = clientsConnected;
    }

    public Collection<ClientPOJO> getClientsConnected() {
        return clientsConnected;
    }
}
