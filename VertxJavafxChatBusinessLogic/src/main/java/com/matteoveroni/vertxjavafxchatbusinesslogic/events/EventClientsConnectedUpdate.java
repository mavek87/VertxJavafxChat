package com.matteoveroni.vertxjavafxchatbusinesslogic.events;

import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ClientPOJO;
import java.util.Iterator;
import java.util.List;

public class EventClientsConnectedUpdate {

    private final List<ClientPOJO> clientsConnected;

    public EventClientsConnectedUpdate(List<ClientPOJO> clientsConnected) {
        this.clientsConnected = clientsConnected;
    }

    public Iterator<ClientPOJO> getClientsConnectedIterator() {
        return clientsConnected.iterator();
    }
}
