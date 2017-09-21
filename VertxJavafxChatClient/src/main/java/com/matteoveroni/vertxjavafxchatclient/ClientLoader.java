package com.matteoveroni.vertxjavafxchatclient;

import com.matteoveroni.vertxjavafxchatclient.events.EventClientShutdown;
import com.matteoveroni.vertxjavafxchatclient.events.EventGUIShutdown;
import com.matteoveroni.vertxjavafxchatclient.events.EventLoginToChat;
import com.matteoveroni.vertxjavafxchatclient.gui.ChatGUIController;
import com.matteoveroni.vertxjavafxchatclient.net.verticles.ClockVerticle;
import com.matteoveroni.vertxjavafxchatclient.net.verticles.TcpClientVerticle;
import io.vertx.core.Vertx;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientLoader {

    private static final String FXML_FILE_PATH = "/fxml/ChatGUI.fxml";

    private static final EventBus SYSTEM_EVENT_BUS = EventBus.getDefault();

    private static final Logger LOG = LoggerFactory.getLogger(ClientLoader.class);

    public ClientLoader() {
        SYSTEM_EVENT_BUS.register(this);
    }

    @Subscribe
    public void event(EventLoginToChat event) {

        Stage guiStage = event.getStage();
        String clientNickname = event.getNickname();

        try {

            startJavafxChatGUI(guiStage, clientNickname);
            deployClientVerticles(clientNickname);

        } catch (Exception ex) {
            LOG.error(ex.getMessage());
        }
    }

    private void deployClientVerticles(String nickname) {

        TcpClientVerticle tcpClientVerticle = new TcpClientVerticle(nickname);
        ClockVerticle clockVerticle = new ClockVerticle();

        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(tcpClientVerticle, res -> {
            
            if (!res.succeeded()) {
                vertx.close();
                String exeptionDescription = res.cause().getMessage();
                SYSTEM_EVENT_BUS.postSticky(new EventGUIShutdown(exeptionDescription));
                LOG.error(exeptionDescription);
//                throw new RuntimeException("An exception is occurred: " + exeptionDescription);
            }
            
        });

        vertx.deployVerticle(clockVerticle);

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
