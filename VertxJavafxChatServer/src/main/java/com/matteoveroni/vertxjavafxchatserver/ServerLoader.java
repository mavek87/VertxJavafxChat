package com.matteoveroni.vertxjavafxchatserver;

import com.matteoveroni.vertxjavafxchatbusinesslogic.App;
import com.matteoveroni.vertxjavafxchatserver.net.verticles.TcpServerVerticle;
import io.vertx.core.Vertx;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ServerLoader extends Application {

    private static final String SERVER_GUI_FXML_FILE_PATH = "/fxml/ServerGUI.fxml";

    private static Vertx vertx;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader serverControllerLoader = new FXMLLoader(getClass().getResource(SERVER_GUI_FXML_FILE_PATH));
        Parent serverParentRoot = serverControllerLoader.load();
        Scene serverScene = new Scene(serverParentRoot);
        stage.setScene(serverScene);
        stage.setTitle(App.NAME + " v. " + App.VERSION);
        stage.setResizable(false);
        stage.setAlwaysOnTop(true);
        stage.setOnCloseRequest(closeRequest -> {
            Platform.exit();
        });
        stage.show();
    }

    public static void startServer(String serverAddress, int serverPort) {
        vertx = Vertx.vertx();
        vertx.deployVerticle(new TcpServerVerticle(serverAddress, serverPort));
    }

    public static void stopServer() {

        if (vertx != null) {
            vertx.close(result -> {
                if (result.succeeded()) {
                    System.out.println("Vertx closed");
                } else {
                    System.out.println("Error trying to close vertx");
                }
            });
        }
    }

}
