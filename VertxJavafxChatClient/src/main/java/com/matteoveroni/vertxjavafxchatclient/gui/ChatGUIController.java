package com.matteoveroni.vertxjavafxchatclient.gui;

import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ChatPrivateMessagePOJO;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client.ClientPOJO;
import com.matteoveroni.vertxjavafxchatclient.events.EventReceivedConnectionsUpdateMessage;
import com.matteoveroni.vertxjavafxchatclient.events.EventReceivedChatPrivateMessage;
import com.matteoveroni.vertxjavafxchatclient.events.EventSendChatMessage;
import com.matteoveroni.vertxjavafxchatclient.net.verticles.TcpClientVerticle;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
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
    Label lbl_nickname;

    @FXML
    Button btn_sendToServer;

    @FXML
    TextField txt_message;

    @FXML
    TextArea txtArea_receivedMessages;

    @FXML
    VBox vbox_connectedClients;

    @FXML
    private void handleButtonSendToServerAction(ActionEvent event) {
        String clientAddress = TcpClientVerticle.CLIENT_ADDRESS;
        Integer clientPort = TcpClientVerticle.CLIENT_PORT;
        if (clientPort != null && clientAddress != null) {

            String myNickname = lbl_nickname.getText();
            String messageText = myNickname + ": " + txt_message.getText();

            ClientPOJO messageSource = new ClientPOJO(clientAddress, clientPort);
            ChatPrivateMessagePOJO chatPrivateMessage = new ChatPrivateMessagePOJO(messageSource, messageSource, messageText);
            SYSTEM_EVENT_BUS.postSticky(new EventSendChatMessage(chatPrivateMessage));

            txt_message.clear();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        clearConnectedClientsInTheUI();

        txtArea_receivedMessages.setEditable(false);
        btn_sendToServer.setVisible(false);

        txt_message.textProperty().addListener((ObservableValue<? extends String> obs, String oldTextValue, String newTextValue) -> {
            boolean isTextEmpty = newTextValue.trim().isEmpty();
            btn_sendToServer.setVisible(!isTextEmpty);
        });

        Random randomNickname = new Random();
        lbl_nickname.setText(Integer.toString(randomNickname.nextInt()));
        SYSTEM_EVENT_BUS.register(this);
    }

    @Subscribe
    public void onEvent(EventReceivedConnectionsUpdateMessage event) {
        Platform.runLater(() -> {
            clearConnectedClientsInTheUI();

            for (ClientPOJO client : event.getClientsConnected()) {
                vbox_connectedClients.getChildren().add(new Label(client.toString()));
            }
        });
    }

    @Subscribe
    public void onEvent(EventReceivedChatPrivateMessage event) {
        String privateChatText = event.getChatPrivateMessage().getText();
        if (privateChatText != null) {
            Platform.runLater(() -> {
                txtArea_receivedMessages.appendText(privateChatText + "\n");
            });
        }
    }

    private void clearConnectedClientsInTheUI() {
        vbox_connectedClients.getChildren().clear();
    }
}
