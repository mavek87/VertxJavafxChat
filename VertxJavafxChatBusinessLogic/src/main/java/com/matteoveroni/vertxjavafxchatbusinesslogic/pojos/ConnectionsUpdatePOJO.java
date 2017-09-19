package com.matteoveroni.vertxjavafxchatbusinesslogic.pojos;

import java.util.Collection;

public class ConnectionsUpdatePOJO {

    private Collection<ClientPOJO> clientsConnected;

    public ConnectionsUpdatePOJO() {
    }

    public ConnectionsUpdatePOJO(Collection<ClientPOJO> clientsConnected) {
        this.clientsConnected = clientsConnected;
    }

    public Collection<ClientPOJO> getClientsConnected() {
        return clientsConnected;
    }

    public void setClientsConnected(Collection<ClientPOJO> clientsConnected) {
        this.clientsConnected = clientsConnected;
    }

}
