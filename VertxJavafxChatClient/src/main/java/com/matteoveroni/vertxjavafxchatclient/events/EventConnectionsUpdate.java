package com.matteoveroni.vertxjavafxchatclient.events;

import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client.ClientPOJO;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.server.ServerConnectionsUpdate;
import java.util.Collection;

public class EventConnectionsUpdate {

    private final ServerConnectionsUpdate connectionUpdate;

    public EventConnectionsUpdate(ServerConnectionsUpdate connectionUpdate) {
        this.connectionUpdate = connectionUpdate;
    }

    public Collection<ClientPOJO> getClientsConnected() {
        return connectionUpdate.getClientsConnected();
    }
}
