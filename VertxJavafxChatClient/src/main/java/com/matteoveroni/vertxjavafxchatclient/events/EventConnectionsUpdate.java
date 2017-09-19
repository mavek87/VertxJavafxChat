package com.matteoveroni.vertxjavafxchatclient.events;

import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ClientPOJO;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ConnectionsUpdatePOJO;
import java.util.Collection;

public class EventConnectionsUpdate {

    private final ConnectionsUpdatePOJO connectionUpdate;

    public EventConnectionsUpdate(ConnectionsUpdatePOJO connectionUpdate) {
        this.connectionUpdate = connectionUpdate;
    }

    public Collection<ClientPOJO> getClientsConnected() {
        return connectionUpdate.getClientsConnected();
    }
}
