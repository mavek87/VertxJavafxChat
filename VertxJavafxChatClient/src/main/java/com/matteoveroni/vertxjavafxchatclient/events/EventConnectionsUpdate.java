package com.matteoveroni.vertxjavafxchatclient.events;

import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ClientPOJO;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ConnectionsUpdate;
import java.util.Collection;

public class EventConnectionsUpdate {

    private final ConnectionsUpdate connectionUpdate;

    public EventConnectionsUpdate(ConnectionsUpdate connectionUpdate) {
        this.connectionUpdate = connectionUpdate;
    }

    public Collection<ClientPOJO> getClientsConnected() {
        return connectionUpdate.getClientsConnected();
    }
}
