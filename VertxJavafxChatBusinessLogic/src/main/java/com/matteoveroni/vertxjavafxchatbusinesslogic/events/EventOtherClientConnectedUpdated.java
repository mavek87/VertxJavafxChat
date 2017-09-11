package com.matteoveroni.vertxjavafxchatbusinesslogic.events;

import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ClientPOJO;
import java.util.Iterator;
import java.util.List;

public class EventOtherClientConnectedUpdated {

    private final List<ClientPOJO> otherClientsConnected;

    public EventOtherClientConnectedUpdated(List<ClientPOJO> otherClientsConnected) {
        this.otherClientsConnected = otherClientsConnected;
    }

    public Iterator<ClientPOJO> getOtherClientsConnectedIterator() {
        return otherClientsConnected.iterator();
    }
}
