package com.matteoveroni.vertxjavafxchatclient.events;

import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ClientPOJO;
import java.util.Iterator;

public class EventConnectionsUpdate {

    private final Iterator<ClientPOJO> connectedClients;

    public EventConnectionsUpdate(Iterator<ClientPOJO> connectedClients) {
        this.connectedClients = connectedClients;
    }

    public Iterator<ClientPOJO> getConnectedClientsIterator() {
        return connectedClients;
    }

}
