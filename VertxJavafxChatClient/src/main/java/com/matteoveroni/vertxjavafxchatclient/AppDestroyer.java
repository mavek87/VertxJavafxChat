package com.matteoveroni.vertxjavafxchatclient;

import com.matteoveroni.vertxjavafxchatclient.events.EventDestroyApp;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppDestroyer {

    private static final Logger LOG = LoggerFactory.getLogger(AppDestroyer.class);

    private final Vertx vertxInstance;

    public AppDestroyer(Vertx vertxInstance) {
        this.vertxInstance = vertxInstance;

        EventBus vertxEventBus = this.vertxInstance.eventBus();

        vertxEventBus.consumer(EventDestroyApp.BUS_ADDRESS, message -> {
            String exceptionMessage = (String) message.body();
            if (exceptionMessage == null) {
                closeApp();
            } else {
                closeAppWithError(exceptionMessage);
            }
        });

    }

    private void closeAppWithError(String exceptionMessage) {
        LOG.error(exceptionMessage);

        Platform.runLater(() -> {
            LOG.error(exceptionMessage);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An error occurred. Application will be closed");
            alert.setContentText("Error details: " + exceptionMessage);
            alert.showAndWait();

            closeApp();
        });

    }

    private void closeApp() {
        Platform.exit();
        if (vertxInstance != null) {
            vertxInstance.close();
        }
    }

}
