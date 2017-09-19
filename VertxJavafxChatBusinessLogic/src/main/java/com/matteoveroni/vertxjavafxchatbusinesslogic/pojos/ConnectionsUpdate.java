package com.matteoveroni.vertxjavafxchatbusinesslogic.pojos;

import java.util.Collection;

public class ConnectionsUpdate {

    private Collection<ClientPOJO> clientsConnected;

    public ConnectionsUpdate() {
    }

    public ConnectionsUpdate(Collection<ClientPOJO> clientsConnected) {
        this.clientsConnected = clientsConnected;
    }

    public Collection<ClientPOJO> getClientsConnected() {
        return clientsConnected;
    }

    public void setClientsConnected(Collection<ClientPOJO> clientsConnected) {
        this.clientsConnected = clientsConnected;
    }

}
