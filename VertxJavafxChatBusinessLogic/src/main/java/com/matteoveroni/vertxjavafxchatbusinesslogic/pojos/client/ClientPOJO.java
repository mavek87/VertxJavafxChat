package com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client;

import java.util.Objects;

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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + Objects.hashCode(this.address);
        hash = 17 * hash + this.port;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ClientPOJO other = (ClientPOJO) obj;
        if (this.port != other.port) {
            return false;
        }
        if (!Objects.equals(this.address, other.address)) {
            return false;
        }
        return true;
    }   
}