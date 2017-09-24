package com.matteoveroni.vertxjavafxchatclient.gui;

import com.matteoveroni.vertxjavafxchatclient.ClientLoader;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoginGUIController implements Initializable {

    @FXML
    AnchorPane rootPane;

    @FXML
    TextField txt_nickname;

    @FXML
    Button btn_login;

    private ClientLoader clientLoader;

    public void setClientLoader(ClientLoader clientLoader) {
        this.clientLoader = clientLoader;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btn_login.setVisible(false);

        txt_nickname.textProperty().addListener((ObservableValue<? extends String> obs, String oldText, String newText) -> {
            boolean isTextEmpty = newText.trim().isEmpty();
            btn_login.setVisible(!isTextEmpty);
        });
    }

    @FXML
    private void handleButtonLoginAction(ActionEvent event) {
        String myNickname = txt_nickname.getText();
        Stage myStage = (Stage) rootPane.getScene().getWindow();

        if (!myNickname.trim().isEmpty()) {

            if (clientLoader == null) {
                throw new RuntimeException("Unexpected error loading the client. Null clientLoader passed to LoginGuiController!");
            }

            clientLoader.loadClient(myStage, myNickname);
        }
    }

}
