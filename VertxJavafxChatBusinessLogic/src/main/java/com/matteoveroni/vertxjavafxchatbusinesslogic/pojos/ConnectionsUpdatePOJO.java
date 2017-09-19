package com.matteoveroni.vertxjavafxchatbusinesslogic.pojos;

import java.util.Collection;
import java.util.Iterator;

public class ConnectionsUpdatePOJO {

    private final Collection<ClientPOJO> clientsConnected;

    public ConnectionsUpdatePOJO(Collection<ClientPOJO> clientsConnected) {
        this.clientsConnected = clientsConnected;
    }

    public Iterator<ClientPOJO> getClientsConnectedIterator() {
        return clientsConnected.iterator();
    }
}
