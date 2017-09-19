package com.matteoveroni.vertxjavafxchatserver;

import com.matteoveroni.vertxjavafxchatserver.net.verticles.TcpServerVerticle;
import io.vertx.core.Vertx;

public class ServerLoader {

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(new TcpServerVerticle());
    }
}
