package com.matteoveroni.vertxjavafxchatclient.net;

import io.vertx.core.AbstractVerticle;

public class TimerVerticle extends AbstractVerticle {

    public static final String BUS_ADDRESS_TIMER = "timeraddress";

    public static final int TIMER_CLOCK_IN_MILLIS = 1000;

    @Override
    public void start() throws Exception {
        
        vertx.setPeriodic(TIMER_CLOCK_IN_MILLIS, id -> {
            vertx.eventBus().publish(BUS_ADDRESS_TIMER, null);
        });
        
    }
}
