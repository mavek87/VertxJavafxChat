package com.matteoveroni.vertxjavafxchatclient;

import com.matteoveroni.vertxjavafxchatbusinesslogic.App;
import com.matteoveroni.vertxjavafxchatclient.gui.LoginGUIController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import static javafx.application.Application.launch;

public class MainApp extends Application {

    private final ClientHandler clientHandler = new ClientHandler();
    private final AppDestroyer appDestroyer = new AppDestroyer(clientHandler.getVertxInstance());

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        buildAndShowLoginScene(stage);
    }

    private void buildAndShowLoginScene(Stage stage) throws Exception{
        FXMLLoader loginControllerLoader = new FXMLLoader(getClass().getResource("/fxml/LoginGUI.fxml"));
        Parent loginParentRoot = loginControllerLoader.load();
        LoginGUIController loginGUIController = loginControllerLoader.<LoginGUIController>getController();
        loginGUIController.setClientHandler(clientHandler);
        Scene loginScene = new Scene(loginParentRoot);
        stage.setScene(loginScene);
        stage.setTitle(App.NAME + " v. " + App.VERSION);
        stage.setResizable(false);
        stage.show();
    }

}
