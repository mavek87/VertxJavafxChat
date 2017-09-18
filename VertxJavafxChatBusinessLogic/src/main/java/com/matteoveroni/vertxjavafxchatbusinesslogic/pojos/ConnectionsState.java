package com.matteoveroni.vertxjavafxchatbusinesslogic.pojos;

import java.util.Collection;

public class ConnectionsState {

    private final Collection<ClientPOJO> connectedClients;

    public ConnectionsState(Collection<ClientPOJO> connectedClients) {
        this.connectedClients = connectedClients;
    }

    public Collection<ClientPOJO> getConnectedClients() {
        return connectedClients;
    }
}
