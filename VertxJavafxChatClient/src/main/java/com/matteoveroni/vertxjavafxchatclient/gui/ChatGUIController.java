package com.matteoveroni.vertxjavafxchatclient.gui;

import com.matteoveroni.vertxjavafxchatclient.events.EventClockUpdate;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ChatBroadcastMessagePOJO;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ChatPrivateMessagePOJO;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client.ClientPOJO;
import com.matteoveroni.vertxjavafxchatclient.events.EventReceivedChatBroadcastMessage;
import com.matteoveroni.vertxjavafxchatclient.events.EventReceivedConnectionsUpdateMessage;
import com.matteoveroni.vertxjavafxchatclient.events.EventReceivedChatPrivateMessage;
import com.matteoveroni.vertxjavafxchatclient.events.EventSendChatBroadcastMessage;
import com.matteoveroni.vertxjavafxchatclient.events.EventSendChatPrivateMessage;
import com.matteoveroni.vertxjavafxchatclient.net.verticles.TcpClientVerticle;
import io.vertx.core.Vertx;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ChatGUIController implements Initializable {

    private final EventBus SYSTEM_EVENT_BUS = EventBus.getDefault();

    @FXML
    AnchorPane rootPane;

    @FXML
    Label lbl_nickname;

    @FXML
    Label lbl_date;

    @FXML
    ListView<ClientPOJO> listView_connectedHosts;

    @FXML
    Button btn_sendToServer;

    @FXML
    Button btn_clearHostSelection;

    @FXML
    Button btn_clearChatHistory;

    @FXML
    TextField txt_message;

    @FXML
    TextArea txtArea_receivedMessages;

    private final ObservableList<ClientPOJO> obsList_connectedHosts = FXCollections.<ClientPOJO>observableArrayList();

    private String nickname;
    private String currentDate;
    private String currentTime;
    private Vertx vertx;
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
        lbl_nickname.setText(nickname);
    }
    
    public void setVertxInstance(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtArea_receivedMessages.setEditable(false);
        btn_sendToServer.setVisible(false);

        txt_message.textProperty().addListener((ObservableValue<? extends String> obs, String oldTextValue, String newTextValue) -> {
            boolean isTextEmpty = newTextValue.trim().isEmpty();
            btn_sendToServer.setVisible(!isTextEmpty);
        });

        listView_connectedHosts.requestFocus();

        listView_connectedHosts.setCellFactory(param -> new ListCell<ClientPOJO>() {
            @Override
            protected void updateItem(ClientPOJO item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.getAddress() == null) {
                    setText(null);
                } else {
                    setText(item.getNickname() + " - (" + item.getAddress() + " : " + item.getPort() + ")");
                }
            }
        });

        listView_connectedHosts.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends ClientPOJO> observable, ClientPOJO oldValue, ClientPOJO newValue) -> {
            if (newValue != null) {
                btn_sendToServer.setText("Send private message");
            } else {
                btn_sendToServer.setText("Send public message");
            }
        });

        listView_connectedHosts.setItems(obsList_connectedHosts);

        SYSTEM_EVENT_BUS.register(this);
    }

    @FXML
    private void handleButtonSendToServerAction(ActionEvent event) {
        String clientAddress = TcpClientVerticle.CLIENT_ADDRESS;
        Integer clientPort = TcpClientVerticle.CLIENT_PORT;

        if (clientPort != null && clientAddress != null) {

            String message = txt_message.getText();
            String messageToSend = nickname + ": " + txt_message.getText();

            ClientPOJO messageSourceHost = new ClientPOJO(nickname, clientAddress, clientPort);
            ClientPOJO messageTargetHost = listView_connectedHosts.getSelectionModel().getSelectedItem();

            if (messageTargetHost != null) {
                ChatPrivateMessagePOJO chatPrivateMessage = new ChatPrivateMessagePOJO(messageSourceHost, messageTargetHost, messageToSend);
                SYSTEM_EVENT_BUS.postSticky(new EventSendChatPrivateMessage(chatPrivateMessage));

                txtArea_receivedMessages.appendText(currentTime + " - (Private) - " + nickname + " => " + messageTargetHost.getNickname() + ": " + message + "\n");
            } else {
                ChatBroadcastMessagePOJO chatBroadcastMessage = new ChatBroadcastMessagePOJO(messageSourceHost, messageToSend);
                SYSTEM_EVENT_BUS.postSticky(new EventSendChatBroadcastMessage(chatBroadcastMessage));
            }

            txt_message.clear();
        }
    }

    @FXML
    private void handleButtonClearHostSelectionAction(ActionEvent event) {
        listView_connectedHosts.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleButtonClearChatHistoryAction(ActionEvent event) {
        txtArea_receivedMessages.clear();
    }

    @Subscribe
    public void onEvent(EventReceivedConnectionsUpdateMessage event) {
        Platform.runLater(() -> {
            obsList_connectedHosts.clear();
            obsList_connectedHosts.setAll(event.getClientsConnected());
            listView_connectedHosts.setItems(obsList_connectedHosts);
        });
    }

    @Subscribe
    public void onEvent(EventReceivedChatPrivateMessage event) {
        String privateChatText = event.getChatPrivateMessage().getText();
        if (privateChatText != null) {
            Platform.runLater(() -> {
                txtArea_receivedMessages.appendText(currentTime + " - (Private) - " + privateChatText + "\n");
            });
        }
    }

    @Subscribe
    public void onEvent(EventReceivedChatBroadcastMessage event) {
        String broadcastChatText = event.getChatBroadcastMessage().getText();
        if (broadcastChatText != null) {
            Platform.runLater(() -> {
                txtArea_receivedMessages.appendText(currentTime + " - (Public) - " + broadcastChatText + "\n");
            });
        }
    }

    @Subscribe
    public void onEvent(EventClockUpdate event) {
        currentDate = event.getDateAndTime().getDate();
        currentTime = event.getDateAndTime().getTime();
        Platform.runLater(() -> {
            lbl_date.setText("Date: " + currentDate);
        });
    }
}
