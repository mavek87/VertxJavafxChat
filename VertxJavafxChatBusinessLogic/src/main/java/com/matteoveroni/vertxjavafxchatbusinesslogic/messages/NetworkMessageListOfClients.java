package com.matteoveroni.vertxjavafxchatbusinesslogic.messages;

import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ClientPOJO;
import java.util.List;

public class NetworkMessageListOfClients {

    private final List<ClientPOJO> listOfClients;

    public NetworkMessageListOfClients(List<ClientPOJO> listOfClients) {
        this.listOfClients = listOfClients;
    }

    public List<ClientPOJO> getListOfClients() {
        return listOfClients;
    }

}
