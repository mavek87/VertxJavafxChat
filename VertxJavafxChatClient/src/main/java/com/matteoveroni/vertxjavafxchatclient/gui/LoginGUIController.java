package com.matteoveroni.vertxjavafxchatclient.gui;

import com.matteoveroni.vertxjavafxchatclient.events.EventLoginToChat;
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
import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginGUIController implements Initializable {

    private static final Logger LOG = LoggerFactory.getLogger(LoginGUIController.class);
    private final EventBus SYSTEM_EVENT_BUS = EventBus.getDefault();

    @FXML
    AnchorPane rootPane;

    @FXML
    TextField txt_nickname;

    @FXML
    Button btn_login;

    @FXML
    private void handleButtonLoginAction(ActionEvent event) {
        String myNickname = txt_nickname.getText();
        Stage myStage = (Stage) rootPane.getScene().getWindow();

        if (!myNickname.trim().isEmpty()) {
            SYSTEM_EVENT_BUS.postSticky(new EventLoginToChat(myStage, myNickname));
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btn_login.setVisible(false);

        txt_nickname.textProperty().addListener((ObservableValue<? extends String> obs, String oldText, String newText) -> {
            boolean isTextEmpty = newText.trim().isEmpty();
            btn_login.setVisible(!isTextEmpty);
        });
    }

}
