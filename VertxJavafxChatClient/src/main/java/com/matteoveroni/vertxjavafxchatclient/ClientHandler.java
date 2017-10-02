package com.matteoveroni.vertxjavafxchatclient;

import com.matteoveroni.vertxjavafxchatclient.events.EventCloseGUIRequest;
import com.matteoveroni.vertxjavafxchatclient.events.EventDestroyApp;
import com.matteoveroni.vertxjavafxchatclient.gui.ChatGUIController;
import com.matteoveroni.vertxjavafxchatclient.network.verticles.ClockVerticle;
import com.matteoveroni.vertxjavafxchatclient.network.verticles.TcpClientVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientHandler {

    private static final String FXML_FILE_PATH = "/fxml/ChatGUI.fxml";

    private final Vertx vertx = Vertx.vertx();
    private final EventBus vertxEventBus = vertx.eventBus();

    public Vertx getVertxInstance() {
        return vertx;
    }

    public void startClient(String serverAddress, Integer serverPort, String nickname, Stage guiStage) {
        final TcpClientVerticle tcpClientVerticle = new TcpClientVerticle(serverAddress, serverPort, nickname);
        final ClockVerticle clockVerticle = new ClockVerticle();

        vertx.deployVerticle(clockVerticle);
        vertx.deployVerticle(tcpClientVerticle, deployStatus -> {
            if (deployStatus.succeeded()) {
                startJavafxChatGUI(guiStage, nickname);
            } else {
                vertxEventBus.publish(EventDestroyApp.BUS_ADDRESS, deployStatus.cause().getMessage());
            }
        });
    }

    private void startJavafxChatGUI(Stage stage, String nickname) {
        Platform.runLater(() -> {
            try {
                FXMLLoader chatControllerLoader = new FXMLLoader(getClass().getResource(FXML_FILE_PATH));
                Parent chatParentRoot = chatControllerLoader.load();
                ChatGUIController chatGUIController = chatControllerLoader.<ChatGUIController>getController();
                chatGUIController.injectSettings(vertx, nickname);

                Scene chatScene = new Scene(chatParentRoot);
                stage.setScene(chatScene);
                stage.setOnCloseRequest(event -> {
                    vertxEventBus.send(EventCloseGUIRequest.BUS_ADDRESS, null, result -> {
                        if (result.succeeded()) {
                            vertxEventBus.publish(EventDestroyApp.BUS_ADDRESS, null);
                        }
                    });
                });
            } catch (Exception ex) {
                vertxEventBus.publish(EventDestroyApp.BUS_ADDRESS, ex.getMessage());
            }
        });
    }
}
