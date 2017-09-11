package com.matteoveroni.vertxjavafxchatbusinesslogic.pojos;

public class ClientPOJO {

    private final String address;
    private final int port;

    public ClientPOJO(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "Client{" + "address: " + address + ", port: " + port + '}';
    }
}
