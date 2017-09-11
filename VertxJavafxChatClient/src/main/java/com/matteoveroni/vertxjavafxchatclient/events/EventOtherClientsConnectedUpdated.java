package com.matteoveroni.vertxjavafxchatclient.events;

import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ClientPOJO;
import java.util.Iterator;
import java.util.List;

public class EventOtherClientsConnectedUpdated {

    private final List<ClientPOJO> otherClientsConnected;

    public EventOtherClientsConnectedUpdated(List<ClientPOJO> otherClientsConnected) {
        this.otherClientsConnected = otherClientsConnected;
    }

    public Iterator<ClientPOJO> getOtherClientsConnectedIterator() {
        return otherClientsConnected.iterator();
    }
}
