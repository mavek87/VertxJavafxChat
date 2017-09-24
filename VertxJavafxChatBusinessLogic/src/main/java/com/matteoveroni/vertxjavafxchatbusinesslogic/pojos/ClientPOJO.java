package com.matteoveroni.vertxjavafxchatbusinesslogic.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.Json;
import java.util.Objects;

public class ClientPOJO {

    private String nickname;
    private final String address;
    private final int port;

    public ClientPOJO(
        @JsonProperty("nickname") String nickname,
        @JsonProperty("address") String address,
        @JsonProperty("port") int port
    ) {
        this.nickname = nickname;
        this.address = address;
        this.port = port;
    }

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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public int hashCode() {
        int hash = 5;
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

    @Override
    public String toString() {
        return Json.encode(this);
    }
}
