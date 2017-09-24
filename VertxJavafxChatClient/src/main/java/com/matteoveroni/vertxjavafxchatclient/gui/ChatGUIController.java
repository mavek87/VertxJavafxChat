package com.matteoveroni.vertxjavafxchatclient.gui;

import com.google.gson.Gson;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ChatBroadcastMessagePOJO;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ChatPrivateMessagePOJO;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.DateAndTimePOJO;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client.ClientPOJO;
import com.matteoveroni.vertxjavafxchatclient.events.EventReceivedChatBroadcastMessage;
import com.matteoveroni.vertxjavafxchatclient.events.EventReceivedConnectionsUpdateMessage;
import com.matteoveroni.vertxjavafxchatclient.events.EventReceivedChatPrivateMessage;
import com.matteoveroni.vertxjavafxchatclient.events.EventSendChatBroadcastMessage;
import com.matteoveroni.vertxjavafxchatclient.events.EventSendChatPrivateMessage;
import com.matteoveroni.vertxjavafxchatclient.net.verticles.ClockVerticle;
import com.matteoveroni.vertxjavafxchatclient.net.verticles.TcpClientVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
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
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.greenrobot.eventbus.Subscribe;

public class ChatGUIController implements Initializable {

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

    private static final org.greenrobot.eventbus.EventBus SYSTEM_EVENT_BUS = org.greenrobot.eventbus.EventBus.getDefault();
    private static final Gson GSON = new Gson();

    private EventBus vertxEventBus;
    private String nickname;
    private String currentDate;
    private String currentTime;

    private final ObservableList<ClientPOJO> observableList_connectedHosts = FXCollections.<ClientPOJO>observableArrayList();

    public void injectSettings(Vertx vertx, String nickname) {
        this.nickname = nickname;
        lbl_nickname.setText(nickname);

        vertxEventBus = vertx.eventBus();
        vertxEventBus.consumer(ClockVerticle.CLOCK_EVENT_ADDRESS, clockEvent -> {
            DateAndTimePOJO updatedDateAndTime = GSON.fromJson((String) clockEvent.body(), DateAndTimePOJO.class);
            onClockEvent(updatedDateAndTime);
        });
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
        listView_connectedHosts.setCellFactory(param -> new ConnectedHostsListCell());
        listView_connectedHosts.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends ClientPOJO> observable, ClientPOJO oldValue, ClientPOJO newValue) -> {
            if (newValue != null) {
                btn_sendToServer.setText("Send private message");
            } else {
                btn_sendToServer.setText("Send public message");
            }
        });

        listView_connectedHosts.setItems(observableList_connectedHosts);

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
                sendChatPrivateMessageToServer(messageSourceHost, messageTargetHost, messageToSend);
                txtArea_receivedMessages.appendText(currentTime + " - (Private) - " + nickname + " => " + messageTargetHost.getNickname() + ": " + message + "\n");
            } else {
                sendChatPublicMessageToServer(messageSourceHost, messageToSend);
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

    private void sendChatPrivateMessageToServer(ClientPOJO messageSourceHost, ClientPOJO messageTargetHost, String messageText) {
        ChatPrivateMessagePOJO chatPrivateMessage = new ChatPrivateMessagePOJO(messageSourceHost, messageTargetHost, messageText);
        String jsonString_chatPrivateMessage = GSON.toJson(chatPrivateMessage, ChatPrivateMessagePOJO.class);
        vertxEventBus.publish(EventSendChatPrivateMessage.BUS_ADDRESS, jsonString_chatPrivateMessage);
    }

    private void sendChatPublicMessageToServer(ClientPOJO messageSourceHost, String messageText) {
        ChatBroadcastMessagePOJO chatBroadcastMessage = new ChatBroadcastMessagePOJO(messageSourceHost, messageText);
        String jsonString_chatBroadcastMessage = GSON.toJson(chatBroadcastMessage, ChatBroadcastMessagePOJO.class);
        vertxEventBus.publish(EventSendChatBroadcastMessage.BUS_ADDRESS, jsonString_chatBroadcastMessage);
    }

    private void onClockEvent(DateAndTimePOJO dateAndTime) {
        currentDate = dateAndTime.getDate();
        currentTime = dateAndTime.getTime();

        Platform.runLater(() -> {
            lbl_date.setText("Date: " + currentDate);
        });
    }

    @Subscribe
    public void onEvent(EventReceivedConnectionsUpdateMessage event) {
        Platform.runLater(() -> {
            observableList_connectedHosts.clear();
            observableList_connectedHosts.setAll(event.getClientsConnected());
            listView_connectedHosts.setItems(observableList_connectedHosts);
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
}
