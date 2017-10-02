package com.matteoveroni.vertxjavafxchatbusinesslogic.network.messages;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "class") 
public interface NetworkMessage {

    public NetworkMessageType getMessageType();
}
