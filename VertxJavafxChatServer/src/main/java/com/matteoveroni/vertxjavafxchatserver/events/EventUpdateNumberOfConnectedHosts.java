package com.matteoveroni.vertxjavafxchatserver.events;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EventUpdateNumberOfConnectedHosts {

    public static final String BUS_ADDRESS = "event_nmb_of_connected_hosts_updated";

    private final int numberOfConnectedHosts;

    public EventUpdateNumberOfConnectedHosts(@JsonProperty("numberOfConnectedHosts") int numberOfConnectedHosts) {
        this.numberOfConnectedHosts = numberOfConnectedHosts;
    }

    public int getNumberOfConnectedHosts() {
        return numberOfConnectedHosts;
    }
}
