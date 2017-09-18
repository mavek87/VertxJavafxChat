package com.matteoveroni.vertxjavafxchatbusinesslogic.events;

import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ClientPOJO;
import java.util.Collection;
import java.util.Iterator;

public class EventClientsConnectedUpdate {

    private final Collection<ClientPOJO> clientsConnected;

    public EventClientsConnectedUpdate(Collection<ClientPOJO> clientsConnected) {
        this.clientsConnected = clientsConnected;
    }

    public Iterator<ClientPOJO> getClientsConnectedIterator() {
        return clientsConnected.iterator();
    }
}
