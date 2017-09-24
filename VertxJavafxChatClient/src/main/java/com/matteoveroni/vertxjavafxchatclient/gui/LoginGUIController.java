package com.matteoveroni.vertxjavafxchatclient.gui;

import com.matteoveroni.vertxjavafxchatclient.ClientLoader;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoginGUIController implements Initializable {

    @FXML
    AnchorPane rootPane;

    @FXML
    TextField txt_serverAddress;

    @FXML
    TextField txt_serverPort;

    @FXML
    TextField txt_nickname;

    @FXML
    Button btn_login;

    private String serverAddress;
    private Integer serverPort;
    private String nickname;

    private ClientLoader clientLoader;

    public void setClientLoader(ClientLoader clientLoader) {
        this.clientLoader = clientLoader;
    }

    private final BooleanProperty areAllMandatoryFieldsFilled = new SimpleBooleanProperty(false);
    private final boolean[] mandatoryFieldsFilledValues = {true, true, false};
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btn_login.setVisible(false);

        txt_serverAddress.textProperty().addListener((ObservableValue<? extends String> obs, String oldText, String newText) -> {
            boolean isTextFilled = !newText.trim().isEmpty();
            mandatoryFieldsFilledValues[0] = isTextFilled;
            checkIfAllMandatoryFieldsAreFilled();
        });

        txt_serverPort.textProperty().addListener((ObservableValue<? extends String> obs, String oldText, String newText) -> {
            boolean isTextFilled = !newText.trim().isEmpty();
            mandatoryFieldsFilledValues[1] = isTextFilled;
            checkIfAllMandatoryFieldsAreFilled();
        });

        txt_nickname.textProperty().addListener((ObservableValue<? extends String> obs, String oldText, String newText) -> {
            boolean isTextFilled = !newText.trim().isEmpty();
            mandatoryFieldsFilledValues[2] = isTextFilled;
            checkIfAllMandatoryFieldsAreFilled();
        });

        areAllMandatoryFieldsFilled.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean allMandatoryFieldsFilled) -> {
            btn_login.setVisible(allMandatoryFieldsFilled);
        });

    }

    private void checkIfAllMandatoryFieldsAreFilled() {
        for (boolean isMandatoryFieldFilled : mandatoryFieldsFilledValues) {
            if (isMandatoryFieldFilled == false) {
                areAllMandatoryFieldsFilled.set(false);
                return;
            }
        }
        areAllMandatoryFieldsFilled.set(true);
    }

    @FXML
    private void handleButtonLoginAction(ActionEvent event) {
        String serverAddressText = txt_serverAddress.getText();
        String serverPortText = txt_serverPort.getText().trim();
        String nicknameText = txt_nickname.getText().trim();

        serverAddress = (serverAddressText.trim().isEmpty()) ? null : serverAddressText;
        serverPort = (serverPortText.trim().isEmpty()) ? null : Integer.valueOf(serverPortText);
        nickname = (nicknameText.trim().isEmpty()) ? null : nicknameText;

        Stage javafxStage = (Stage) rootPane.getScene().getWindow();

        if (serverAddress == null || serverPort == null || nickname == null) {
            // This should never happen
            showMandatoryFieldsNotSetError();
        } else {

            if (clientLoader == null) {
                throw new RuntimeException("Unexpected error loading the client. Null clientLoader passed to LoginGuiController!");
            }

            clientLoader.loadClient(serverAddress, serverPort, nickname, javafxStage);
        }
    }

    private void showMandatoryFieldsNotSetError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("All the fields are mandatory");
        alert.setContentText("All the fields are mandatory. Fill all of them!");
        alert.showAndWait();
    }

}
