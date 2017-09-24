package com.matteoveroni.vertxjavafxchatclient;

import com.matteoveroni.vertxjavafxchatbusinesslogic.App;
import com.matteoveroni.vertxjavafxchatclient.gui.LoginGUIController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import static javafx.application.Application.launch;

public class ClientApp extends Application {

    private static final String LOGIN_GUI_FXML_FILE_PATH = "/fxml/LoginGUI.fxml";

    private final ClientLoader clientLoader = new ClientLoader();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loginControllerLoader = new FXMLLoader(getClass().getResource(LOGIN_GUI_FXML_FILE_PATH));
        Parent loginParentRoot = loginControllerLoader.load();

        LoginGUIController loginGUIController = loginControllerLoader.<LoginGUIController>getController();
        loginGUIController.setClientLoader(clientLoader);

        buildAndShowLoginScene(stage, loginParentRoot);
    }

    private void buildAndShowLoginScene(Stage stage, Parent loginSceneRoot) {
        Scene loginScene = new Scene(loginSceneRoot);
        stage.setScene(loginScene);
        stage.setTitle(App.NAME + " v. " + App.VERSION);
        stage.setResizable(false);
        stage.show();
    }

}
