package com.matteoveroni.vertxjavafxchatserver.gui;

import com.matteoveroni.vertxjavafxchatbusinesslogic.DefaultHostParameters;
import com.matteoveroni.vertxjavafxchatserver.ServerLoader;
import com.matteoveroni.vertxjavafxchatserver.events.EventUpdateNumberOfConnectedHosts;
import com.matteoveroni.vertxjavafxchatserver.events.EventServerDeploymentError;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerGUIController implements Initializable {

    private static final Logger LOG = LoggerFactory.getLogger(ServerGUIController.class);

    @FXML
    private AnchorPane rootPane;

    @FXML
    private TextField txt_serverPort;

    @FXML
    private Button btn_stopServer;

    @FXML
    private Button btn_startServer;

    @FXML
    private TextField txt_serverStatus;

    @FXML
    private TextField txt_serverAddress;

    @FXML
    private TextField txt_numberOfConnectedHosts;

    private static final String STR_SERVER_RUNNING = "RUNNING";
    private static final String STR_SERVER_NOT_RUNNING = "STOPPED";

    private Vertx vertx;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setDefaultServerParameters();
        setServerStateRunning(false);
        setNumberOfConnectedHosts(0);
    }

    @FXML
    void handleButtonStartServerAction(ActionEvent evt) {
        String str_serverAddress = txt_serverAddress.getText();
        String str_serverPort = txt_serverPort.getText();

        if (str_serverAddress.trim().isEmpty() || str_serverPort.trim().isEmpty()) {
            return;
        }

        int int_serverPort = Integer.valueOf(str_serverPort);

        vertx = ServerLoader.startServer(str_serverAddress, int_serverPort);

        EventBus vertxEventBus = vertx.eventBus();

        vertxEventBus.consumer(EventUpdateNumberOfConnectedHosts.BUS_ADDRESS, busMessage -> {
            EventUpdateNumberOfConnectedHosts event = ((JsonObject) busMessage.body()).mapTo(EventUpdateNumberOfConnectedHosts.class);
            handleEvent(event);
        });

        vertxEventBus.consumer(EventServerDeploymentError.BUS_ADDRESS, busMessage -> {
            EventServerDeploymentError event = ((JsonObject) busMessage.body()).mapTo(EventServerDeploymentError.class);
            handleEvent(event);
        });

        setServerStateRunning(true);
    }

    @FXML
    void handleButtonStopServerAction(ActionEvent event) {
        ServerLoader.stopServer();
        setServerStateRunning(false);
    }

    private void setServerStateRunning(boolean isServerRunning) {
        LOG.info("Server state changed. Is server running? -> " + isServerRunning);

        if (isServerRunning) {
            txt_serverAddress.setEditable(false);
            txt_serverPort.setEditable(false);
            btn_startServer.setDisable(true);
            btn_stopServer.setDisable(false);
            txt_serverStatus.setText(STR_SERVER_RUNNING);
            txt_serverStatus.getStyleClass().remove("red-background-textfield");
            txt_serverStatus.getStyleClass().add("green-background-textfield");
        } else {
            txt_serverAddress.setEditable(true);
            txt_serverPort.setEditable(true);
            btn_startServer.setDisable(false);
            btn_stopServer.setDisable(true);
            txt_serverStatus.setText(STR_SERVER_NOT_RUNNING);
            txt_serverStatus.getStyleClass().remove("green-background-textfield");
            txt_serverStatus.getStyleClass().add("red-background-textfield");
            setNumberOfConnectedHosts(0);
        }
    }

    public void handleEvent(EventServerDeploymentError event) {
        Platform.runLater(() -> {
            Alert errorAlert = new Alert(AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("An error occurred during the server deployment! Invalid parameters!");
            errorAlert.setContentText("Error details: " + event.getExceptionMessage());
            errorAlert.showAndWait();
            setDefaultServerParameters();
            handleButtonStopServerAction(null);
        });
    }

    public void handleEvent(EventUpdateNumberOfConnectedHosts event) {
        Platform.runLater(() -> {
            setNumberOfConnectedHosts(event.getNumberOfConnectedHosts());
        });
    }

    private void setDefaultServerParameters() {
        txt_serverAddress.setText(DefaultHostParameters.DEFAULT_ADDRESS);
        txt_serverPort.setText(String.valueOf(DefaultHostParameters.DEFAULT_PORT));
    }

    private void setNumberOfConnectedHosts(int numberOfConnectedHosts) {
        txt_numberOfConnectedHosts.setText(String.valueOf(numberOfConnectedHosts));
    }
}
