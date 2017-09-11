package com.matteoveroni.vertxjavafxchatbusinesslogic.pojos;

import java.util.List;

public class ConnectionsState {

    private final List<ClientPOJO> connectedClients;

    public ConnectionsState(List<ClientPOJO> connectedClients) {
        this.connectedClients = connectedClients;
    }

    public List<ClientPOJO> getConnectedClients() {
        return connectedClients;
    }
}
