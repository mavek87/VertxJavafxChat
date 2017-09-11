package com.matteoveroni.vertxjavafxchatclient;

import com.matteoveroni.vertxjavafxchatclient.gui.ChatGUIController;
import com.matteoveroni.vertxjavafxchatclient.net.TcpClientVerticle;
import com.matteoveroni.vertxjavafxchatclient.net.TimerVerticle;
import io.vertx.core.Vertx;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientLoader extends Application {

//    private static final TimerVerticle TIMER_VERTICLE = new TimerVerticle();
    private static final TcpClientVerticle TCP_CLIENT_VERTICLE = new TcpClientVerticle();
    
    public static void main(String[] args) {
        setupAndLaunchVertxClient(args);
        setupAndLaunchJavaFxGUI(args);
    }

    private static void setupAndLaunchVertxClient(String[] args) {
        Vertx vertx = Vertx.vertx();
//        vertx.deployVerticle(TIMER_VERTICLE);
        vertx.deployVerticle(TCP_CLIENT_VERTICLE);
    }

    private static void setupAndLaunchJavaFxGUI(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader chatControllerLoader = new FXMLLoader(getClass().getResource("/fxml/ChatGUI.fxml"));
        Parent root = chatControllerLoader.load();

        // Setup chat controller
        ChatGUIController chatController = chatControllerLoader.getController();

        // Setup and show chat GUI
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }
}
