package com.matteoveroni.vertxjavafxchatclient;

import com.matteoveroni.vertxjavafxchatclient.events.EventClientShutdown;
import com.matteoveroni.vertxjavafxchatclient.events.EventLoginToChat;
import com.matteoveroni.vertxjavafxchatclient.gui.ChatGUIController;
import com.matteoveroni.vertxjavafxchatclient.net.verticles.TcpClientVerticle;
import io.vertx.core.Vertx;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ClientLoader {

    private static final String FXML_FILE_PATH = "/fxml/ChatGUI.fxml";
    private static final TcpClientVerticle TCP_CLIENT_VERTICLE = new TcpClientVerticle();

    private static final EventBus SYSTEM_EVENT_BUS = EventBus.getDefault();
    
    public ClientLoader() {
        SYSTEM_EVENT_BUS.register(this);
    }

    @Subscribe
    public void event(EventLoginToChat event) {
        deployClientVerticles();
        try {
            startJavafxChatGUI(event.getStage(), event.getNickname());
        } catch (Exception ex) {
            throw new RuntimeException("Unable to load Chat GUI. Application will be cloesed!");
        }
    }

    private void deployClientVerticles() {
        Vertx.vertx().deployVerticle(TCP_CLIENT_VERTICLE);
    }

    private void startJavafxChatGUI(Stage stage, String nickname) throws Exception {
        FXMLLoader chatControllerLoader = new FXMLLoader(getClass().getResource(FXML_FILE_PATH));
        Parent chatParentRoot = chatControllerLoader.load();
        ChatGUIController chatGUIController = (ChatGUIController) chatControllerLoader.getController();
        chatGUIController.setNickname(nickname);

        Scene chatScene = new Scene(chatParentRoot);
        stage.setScene(chatScene);
        stage.setOnCloseRequest(event -> {
            SYSTEM_EVENT_BUS.postSticky(new EventClientShutdown());
            SYSTEM_EVENT_BUS.unregister(this);
        });
    }
}
