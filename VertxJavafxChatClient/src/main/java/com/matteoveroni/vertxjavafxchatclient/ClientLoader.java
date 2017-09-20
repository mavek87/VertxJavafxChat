package com.matteoveroni.vertxjavafxchatclient;

import com.matteoveroni.vertxjavafxchatclient.events.EventClientShutdown;
import com.matteoveroni.vertxjavafxchatclient.net.verticles.TcpClientVerticle;
import io.vertx.core.Vertx;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;

public class ClientLoader extends Application {

    private static final String FXML_FILE_PATH = "/fxml/ChatGUI.fxml";
//    private static final TimerVerticle TIMER_VERTICLE = new TimerVerticle();
    private static final TcpClientVerticle TCP_CLIENT_VERTICLE = new TcpClientVerticle();

    public static void main(String[] args) {
        deployClientVerticles();
        launch(args);
    }

    private static void deployClientVerticles() {
        Vertx vertx = Vertx.vertx();
//        vertx.deployVerticle(TIMER_VERTICLE);
        vertx.deployVerticle(TCP_CLIENT_VERTICLE);
    }

    @Override
    public void start(Stage chatStage) throws Exception {
        FXMLLoader chatControllerLoader = new FXMLLoader(getClass().getResource(FXML_FILE_PATH));
        Parent chatParentRoot = chatControllerLoader.load();
        buildAndShowChatScene(chatStage, chatParentRoot);
    }

    private void buildAndShowChatScene(Stage chatStage, Parent chatSceneRoot) {
        Scene chatScene = new Scene(chatSceneRoot);
        chatStage.setScene(chatScene);
        chatStage.setOnCloseRequest(event -> {
            EventBus.getDefault().post(new EventClientShutdown());
        });
        chatStage.show();
    }
}
