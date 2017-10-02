package com.matteoveroni.vertxjavafxchatbusinesslogic.network.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ClientPOJO;
import java.util.Collection;

@JsonPropertyOrder({"messageType", "clientsConnected"})
public class ConnectedHostsUpdateMessage implements NetworkMessage {

    public static final String BUS_ADDRESS = "server_connections_update_msg";

    private final NetworkMessageType messageType = NetworkMessageType.CONNECTED_HOSTS_UPDATE;

    private final Collection<ClientPOJO> clientsConnected;

    public ConnectedHostsUpdateMessage(@JsonProperty("clientsConnected") Collection<ClientPOJO> clientsConnected) {
        this.clientsConnected = clientsConnected;
    }

    @Override
    public NetworkMessageType getMessageType() {
        return messageType;
    }

    public Collection<ClientPOJO> getClientsConnected() {
        return clientsConnected;
    }

}
