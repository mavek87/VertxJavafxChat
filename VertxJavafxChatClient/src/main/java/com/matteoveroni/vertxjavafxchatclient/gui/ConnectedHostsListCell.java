package com.matteoveroni.vertxjavafxchatclient.gui;

import javafx.scene.control.ListCell;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ClientPOJO;

public class ConnectedHostsListCell extends ListCell<ClientPOJO> {

    @Override
    protected void updateItem(ClientPOJO item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null || item.getAddress() == null) {
            setText(null);
        } else {
            setText(item.getNickname() + " - (" + item.getAddress() + " : " + item.getPort() + ")");
        }
    }

}
