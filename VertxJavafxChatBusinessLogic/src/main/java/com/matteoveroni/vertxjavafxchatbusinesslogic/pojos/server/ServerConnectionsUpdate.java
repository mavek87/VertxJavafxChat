package com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.server;

import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client.ClientPOJO;
import java.util.Collection;

public class ServerConnectionsUpdate {

    private final Collection<ClientPOJO> clientsConnected;

    public ServerConnectionsUpdate(Collection<ClientPOJO> clientsConnected) {
        this.clientsConnected = clientsConnected;
    }

    public Collection<ClientPOJO> getClientsConnected() {
        return clientsConnected;
    }
}
