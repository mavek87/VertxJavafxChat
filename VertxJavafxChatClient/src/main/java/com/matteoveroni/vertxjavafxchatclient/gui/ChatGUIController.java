package com.matteoveroni.vertxjavafxchatclient.gui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ChatGUIController implements Initializable {

//    private final EventBus EVENT_BUS = EventBus.getDefault();

    @FXML
    TextField txt_message;

    @FXML
    VBox vbox_connectedClients;

    @FXML
    private void handleButtonSendToServerAction(ActionEvent event) {
        String text = txt_message.getText();
        txt_message.clear();
//        EVENT_BUS.postSticky(new EventMessage(text));
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        clearConnectedClientsInTheUI();
//        EVENT_BUS.register(this);
    }

//    @Subscribe
//    public void onEvent(EventOtherClientsConnectedUpdated event) {
//        clearConnectedClientsInTheUI();
//
//        EventOtherClientsConnectedUpdated eventOtherClientsConnected = (EventOtherClientsConnectedUpdated) event;
//        Iterator<ClientPOJO> otherClientsConnectedIterator = eventOtherClientsConnected.getOtherClientsConnectedIterator();
//
//        while (otherClientsConnectedIterator.hasNext()) {
//            ClientPOJO client = otherClientsConnectedIterator.next();
//            vbox_connectedClients.getChildren().add(new Label(client.toString()));
//        }
//    }

    private void clearConnectedClientsInTheUI() {
        vbox_connectedClients.getChildren().clear();
    }
}
