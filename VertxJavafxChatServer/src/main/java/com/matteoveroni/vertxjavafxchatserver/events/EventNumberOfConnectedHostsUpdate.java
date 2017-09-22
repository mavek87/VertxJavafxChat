package com.matteoveroni.vertxjavafxchatserver.events;

public class EventNumberOfConnectedHostsUpdate {

    private final int numberOfConnectedHosts;

    public EventNumberOfConnectedHostsUpdate(int numberOfConnectedHosts) {
        this.numberOfConnectedHosts = numberOfConnectedHosts;
    }

    public int getNumberOfConnectedHosts() {
        return numberOfConnectedHosts;
    }
}
