package com.matteoveroni.vertxjavafxchatbusinesslogic.pojos;

import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.client.ClientPOJO;

public class ChatPrivateMessagePOJO {

    private String nickname;
    private final ClientPOJO sourceClient;
    private final ClientPOJO targetClient;
    private final String text;

    public ChatPrivateMessagePOJO(ClientPOJO sourceClient, ClientPOJO targetClient, String text) {
        this.sourceClient = sourceClient;
        this.targetClient = targetClient;
        this.text = text;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public ClientPOJO getSourceClient() {
        return sourceClient;
    }

    public ClientPOJO getTargetClient() {
        return targetClient;
    }

    public String getText() {
        return text;
    }

}
