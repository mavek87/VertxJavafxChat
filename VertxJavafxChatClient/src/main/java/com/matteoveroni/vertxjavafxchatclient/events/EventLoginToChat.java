package com.matteoveroni.vertxjavafxchatclient.events;

import javafx.stage.Stage;

public class EventLoginToChat {

    private final Stage stage;
    private final String nickname;

    public EventLoginToChat(Stage stage, String nickname) {
        this.stage = stage;
        this.nickname = nickname;
    }

    public Stage getStage() {
        return stage;
    }

    public String getNickname() {
        return nickname;
    }

}
