package com.matteoveroni.vertxjavafxchatclient.gui;

import com.matteoveroni.vertxjavafxchatbusinesslogic.tcpmessages.ChatBroadcastMessage;
import com.matteoveroni.vertxjavafxchatbusinesslogic.tcpmessages.ChatPrivateMessage;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.DateAndTimePOJO;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.ClientPOJO;
import com.matteoveroni.vertxjavafxchatbusinesslogic.tcpmessages.server.ServerConnectionsUpdateMessage;
import com.matteoveroni.vertxjavafxchatclient.events.EventReceivedChatBroadcastMessage;
import com.matteoveroni.vertxjavafxchatclient.events.EventReceivedChatPrivateMessage;
import com.matteoveroni.vertxjavafxchatclient.events.EventSendChatBroadcastMessage;
import com.matteoveroni.vertxjavafxchatclient.events.EventSendChatPrivateMessage;
import com.matteoveroni.vertxjavafxchatclient.net.verticles.ClockVerticle;
import com.matteoveroni.vertxjavafxchatclient.net.verticles.TcpClientVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

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

    private Vertx vertx;
    private EventBus vertxEventBus;
    private String nickname;
    private String currentDate;
    private String currentTime;

    private final ObservableList<ClientPOJO> observableList_connectedHosts = FXCollections.<ClientPOJO>observableArrayList();

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
    }

    public void injectSettings(Vertx vertx, String nickname) {
        this.nickname = nickname;
        lbl_nickname.setText(nickname);

        this.vertx = vertx;

        vertxEventBus = vertx.eventBus();
        vertxEventBus.consumer(ClockVerticle.CLOCK_EVENT_ADDRESS, clockEvent -> {
            DateAndTimePOJO updatedDateAndTime = ((JsonObject) clockEvent.body()).mapTo(DateAndTimePOJO.class);
            handleEventClockUpdate(updatedDateAndTime);
        });

        vertxEventBus.consumer(TcpClientVerticle.SOCKET_CLOSED_EVENT_ADDRESS, event -> {
            handleEventSocketClosed();
        });

        vertxEventBus.consumer(TcpClientVerticle.SOCKET_ERROR_EVENT_ADDRESS, event -> {
            handleEventSocketError((String) event.body());
        });

        vertxEventBus.consumer(ServerConnectionsUpdateMessage.BUS_ADDRESS, event -> {
            ServerConnectionsUpdateMessage connectionsUpdateMsg = ((JsonObject) event.body()).mapTo(ServerConnectionsUpdateMessage.class);
            handleEventServerConnectionsUpdateMessage(connectionsUpdateMsg.getClientsConnected());
        });

        vertxEventBus.consumer(EventReceivedChatPrivateMessage.BUS_ADDRESS, event -> {
            ChatPrivateMessage privateMessage = ((JsonObject) event.body()).mapTo(ChatPrivateMessage.class);
            handleEventReceivedChatPrivateMessage(privateMessage);
        });

        vertxEventBus.consumer(EventReceivedChatBroadcastMessage.BUS_ADDRESS, event -> {
            ChatBroadcastMessage broadcastMessage = ((JsonObject) event.body()).mapTo(ChatBroadcastMessage.class);
            handleEventReceivedChatBroadcastMessage(broadcastMessage);
        });
    }

    private void handleEventClockUpdate(DateAndTimePOJO dateAndTime) {
        currentDate = dateAndTime.getDate();
        currentTime = dateAndTime.getTime();

        Platform.runLater(() -> {
            lbl_date.setText("Date: " + currentDate);
        });
    }

    private void handleEventSocketClosed() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Server connection closed");
            alert.setContentText("Connection with the server is being lost.\nThe app will be closed!");
            alert.showAndWait();
            vertx.close();
            Platform.exit();
        });
    }

    private void handleEventSocketError(String errorMessage) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Server communication error");
            alert.setContentText(
                "An error occurred during the communication with the server.\n" + errorMessage + "\nPress ok to continue."
            );
            alert.showAndWait();
        });
    }

    private void handleEventServerConnectionsUpdateMessage(Collection<ClientPOJO> connectedClients) {
        Platform.runLater(() -> {
            observableList_connectedHosts.clear();
            observableList_connectedHosts.setAll(connectedClients);
            listView_connectedHosts.setItems(observableList_connectedHosts);
        });
    }

    private void handleEventReceivedChatPrivateMessage(ChatPrivateMessage privateMessage) {
        String privateChatText = privateMessage.getText();
        Platform.runLater(() -> {
            txtArea_receivedMessages.appendText(currentTime + " - (Private) - " + privateChatText + "\n");
        });
    }

    private void handleEventReceivedChatBroadcastMessage(ChatBroadcastMessage broadcastMessage) {
        String broadcastChatText = broadcastMessage.getText();
        Platform.runLater(() -> {
            txtArea_receivedMessages.appendText(currentTime + " - (Public) - " + broadcastChatText + "\n");
        });
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
        ChatPrivateMessage chatPrivateMessage = new ChatPrivateMessage(messageSourceHost, messageTargetHost, messageText);
        JsonObject json_chatPrivateMessage = JsonObject.mapFrom(chatPrivateMessage);
        vertxEventBus.publish(EventSendChatPrivateMessage.BUS_ADDRESS, json_chatPrivateMessage);
    }

    private void sendChatPublicMessageToServer(ClientPOJO messageSourceHost, String messageText) {
        ChatBroadcastMessage chatBroadcastMessage = new ChatBroadcastMessage(messageSourceHost, messageText);
        JsonObject json_chatBraoadcastMessage = JsonObject.mapFrom(chatBroadcastMessage);
        vertxEventBus.publish(EventSendChatBroadcastMessage.BUS_ADDRESS, json_chatBraoadcastMessage);
    }
}
