package com.matteoveroni.vertxjavafxchatserver.gui;

import com.matteoveroni.vertxjavafxchatserver.ServerLoader;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerGUIController implements Initializable {

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

    private static final String STR_SERVER_RUNNING = "SERVER RUNNING";
    private static final String STR_SERVER_NOT_RUNNING = "SERVER STOPPED";

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setServerStateRunning(false);
    }

    private void setServerStateRunning(boolean isServerRunning) {
        LOG.info("Server state changed. Is server running? -> " + isServerRunning);
        if (isServerRunning) {
            btn_startServer.setDisable(true);
            btn_stopServer.setDisable(false);
            txt_serverStatus.setText(STR_SERVER_RUNNING);
        } else {
            btn_startServer.setDisable(false);
            btn_stopServer.setDisable(true);
            txt_serverStatus.setText(STR_SERVER_NOT_RUNNING);
        }
    }

}
