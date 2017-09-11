package com.matteoveroni.vertxjavafxchatserver;

import com.matteoveroni.vertxjavafxchatserver.verticles.TcpServerVerticle;
import io.vertx.core.Vertx;

public class ServerLoader {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new TcpServerVerticle());
    }
}
