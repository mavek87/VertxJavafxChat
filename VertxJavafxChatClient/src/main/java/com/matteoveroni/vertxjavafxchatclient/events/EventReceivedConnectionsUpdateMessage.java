package com.matteoveroni.vertxjavafxchatclient.events;

import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client.ClientPOJO;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.server.ServerConnectionsUpdateMessage;
import java.util.Collection;

public class EventReceivedConnectionsUpdateMessage {

    private final ServerConnectionsUpdateMessage connectionUpdate;

    public EventReceivedConnectionsUpdateMessage(ServerConnectionsUpdateMessage connectionUpdate) {
        this.connectionUpdate = connectionUpdate;
    }

    public Collection<ClientPOJO> getClientsConnected() {
        return connectionUpdate.getClientsConnected();
    }
}
