package com.matteoveroni.vertxjavafxchatclient;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;

public class AppLoader extends Application {

    public static final String APP_NAME = "Vertx-Javafx-Chat";
    public static final String APP_VERSION = "0.2.0";
    private static final String LOGIN_GUI_FXML_FILE_PATH = "/fxml/LoginGUI.fxml";

    public static void main(String[] args) {
        ClientLoader clientLoader = new ClientLoader();
        EventBus.getDefault().register(clientLoader);
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loginControllerLoader = new FXMLLoader(getClass().getResource(LOGIN_GUI_FXML_FILE_PATH));
        Parent loginParentRoot = loginControllerLoader.load();
        buildAndShowChatScene(stage, loginParentRoot);
    }

    private void buildAndShowChatScene(Stage stage, Parent loginSceneRoot) {
        Scene loginScene = new Scene(loginSceneRoot);
        stage.setScene(loginScene);
        stage.setTitle(APP_NAME + " v. " + APP_VERSION);
        stage.setResizable(false);
        stage.setAlwaysOnTop(true);
        stage.show();
    }
}
