package com.matteoveroni.vertxjavafxchatclient.gui;

import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ClientPOJO;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ConnectionsUpdatePOJO;
import com.matteoveroni.vertxjavafxchatclient.events.EventMessage;
import java.net.URL;
import java.util.Iterator;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatGUIController implements Initializable {

    private static final Logger LOG = LoggerFactory.getLogger(ChatGUIController.class);
    private final EventBus SYSTEM_EVENT_BUS = EventBus.getDefault();

    @FXML
    AnchorPane rootPane;

    @FXML
    TextField txt_message;

    @FXML
    VBox vbox_connectedClients;

    @FXML
    private void handleButtonSendToServerAction(ActionEvent event) {
        String text = txt_message.getText();
        txt_message.clear();
        SYSTEM_EVENT_BUS.postSticky(new EventMessage(text));
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        clearConnectedClientsInTheUI();
        Random rand = new Random();
        txt_message.setText(Integer.toString(rand.nextInt()));
        SYSTEM_EVENT_BUS.register(this);
    }

    @Subscribe
    public void onEvent(ConnectionsUpdatePOJO event) {
        Platform.runLater(() -> {
            clearConnectedClientsInTheUI();

            ConnectionsUpdatePOJO connectionsUpdate = (ConnectionsUpdatePOJO) event;
            Iterator<ClientPOJO> connectedClientsIterator = connectionsUpdate.getClientsConnectedIterator();

            while (connectedClientsIterator.hasNext()) {
                ClientPOJO client = connectedClientsIterator.next();
                vbox_connectedClients.getChildren().add(new Label(client.toString()));
            }
        });
    }

    private void clearConnectedClientsInTheUI() {
        vbox_connectedClients.getChildren().clear();
    }
}
