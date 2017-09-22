package com.matteoveroni.vertxjavafxchatserver.gui;

import com.matteoveroni.vertxjavafxchatserver.ServerLoader;
import com.matteoveroni.vertxjavafxchatserver.events.EventServerDeploymentError;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerGUIController implements Initializable {

    private final String DEFAULT_SERVER_ADDRESS = "localhost";
    private final int DEFAULT_SERVER_PORT = 8080;

    private static final Logger LOG = LoggerFactory.getLogger(ServerGUIController.class);
    private final EventBus SYSTEM_EVENT_BUS = EventBus.getDefault();

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

    private static final String STR_SERVER_RUNNING = "SERVER IS RUNNING";
    private static final String STR_SERVER_NOT_RUNNING = "SERVER IS STOPPED";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        SYSTEM_EVENT_BUS.register(this);
        setDefaultServerParameters();
        setServerStateRunning(false);
    }

    @FXML
    void handleButtonStartServerAction(ActionEvent event) {
        setServerStateRunning(true);

        String str_serverAddress = txt_serverAddress.getText();
        String str_serverPort = txt_serverPort.getText();

        if (str_serverAddress.trim().isEmpty() || str_serverPort.trim().isEmpty()) {
            return;
        }

        int serverPort = Integer.valueOf(str_serverPort);

        ServerLoader.startServer(str_serverAddress, serverPort);
    }

    @FXML
    void handleButtonStopServerAction(ActionEvent event) {
        setServerStateRunning(false);
        ServerLoader.stopServer();
    }

    private void setServerStateRunning(boolean isServerRunning) {
        LOG.info("Server state changed. Is server running? -> " + isServerRunning);
        if (isServerRunning) {
            txt_serverAddress.setEditable(false);
            txt_serverPort.setEditable(false);
            btn_startServer.setDisable(true);
            btn_stopServer.setDisable(false);
            txt_serverStatus.setText(STR_SERVER_RUNNING);
        } else {
            txt_serverAddress.setEditable(true);
            txt_serverPort.setEditable(true);
            btn_startServer.setDisable(false);
            btn_stopServer.setDisable(true);
            txt_serverStatus.setText(STR_SERVER_NOT_RUNNING);
        }
    }

    @Subscribe
    public void onEvent(EventServerDeploymentError event) {
        Platform.runLater(() -> {
            Alert errorAlert = new Alert(AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("An error occurred during the server deployment! Invalid parameters!");
            errorAlert.setContentText("Error details: " + event.getExceptionMessage());

            Optional<ButtonType> result = errorAlert.showAndWait();
            setDefaultServerParameters();
            handleButtonStopServerAction(null);
        });
    }

    private void setDefaultServerParameters() {
        txt_serverAddress.setText(DEFAULT_SERVER_ADDRESS);
        txt_serverPort.setText(String.valueOf(DEFAULT_SERVER_PORT));
    }
}
