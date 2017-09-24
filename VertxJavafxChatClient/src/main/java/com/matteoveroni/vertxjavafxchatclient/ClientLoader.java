package com.matteoveroni.vertxjavafxchatclient;

import com.matteoveroni.vertxjavafxchatclient.events.EventClientShutdown;
import com.matteoveroni.vertxjavafxchatclient.gui.ChatGUIController;
import com.matteoveroni.vertxjavafxchatclient.net.verticles.ClockVerticle;
import com.matteoveroni.vertxjavafxchatclient.net.verticles.TcpClientVerticle;
import io.vertx.core.Vertx;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientLoader {

    private static final String FXML_FILE_PATH = "/fxml/ChatGUI.fxml";

    private static final EventBus SYSTEM_EVENT_BUS = EventBus.getDefault();

    private static final Logger LOG = LoggerFactory.getLogger(ClientLoader.class);

    private Vertx vertx;

    public void loadClient(Stage guiStage, String nickname) {

        TcpClientVerticle tcpClientVerticle = new TcpClientVerticle(nickname);
        ClockVerticle clockVerticle = new ClockVerticle();

        vertx = Vertx.vertx();
        vertx.deployVerticle(clockVerticle);
        vertx.deployVerticle(tcpClientVerticle, res -> {

            if (res.succeeded()) {
                try {
                    startJavafxChatGUI(guiStage, nickname);
                } catch (Exception ex) {
                    closeAppWithError(ex.getMessage());
                }
            } else {
                closeAppWithError(res.cause().getMessage());
            }

        });
    }

    private void startJavafxChatGUI(Stage stage, String nickname) {
        Platform.runLater(() -> {

            FXMLLoader chatControllerLoader = new FXMLLoader(getClass().getResource(FXML_FILE_PATH));
            try {

                Parent chatParentRoot = chatControllerLoader.load();
                ChatGUIController chatGUIController = (ChatGUIController) chatControllerLoader.getController();
                chatGUIController.setNickname(nickname);

                Scene chatScene = new Scene(chatParentRoot);
                stage.setScene(chatScene);
                stage.setOnCloseRequest(event -> {
                    closeApp();
                });

            } catch (Exception ex) {
                closeAppWithError(ex.getMessage());
            }

        });
    }

    private void closeAppWithError(String causeExceptionMessage) {

        Platform.runLater(() -> {
            LOG.error(causeExceptionMessage);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An error occurred. Application will be closed");
            alert.setContentText("Error details: " + causeExceptionMessage);
            alert.showAndWait();

            closeApp();
        });

    }

    private void closeApp() {
        SYSTEM_EVENT_BUS.postSticky(new EventClientShutdown());
        if (vertx != null) {
            vertx.close();
        }
        Platform.exit();
    }
}
