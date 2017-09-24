package com.matteoveroni.vertxjavafxchatclient.net.verticles;

import com.google.gson.Gson;
import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.DateAndTimePOJO;
import io.vertx.core.AbstractVerticle;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ClockVerticle extends AbstractVerticle {

    public static final String CLOCK_EVENT_ADDRESS = "clock_event_address";

    private static final int CLOCK_TIME_UNIT_IN_MILLIS = 1000;
    private static final Gson GSON = new Gson();

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public void start() throws Exception {
        
        vertx.setPeriodic(CLOCK_TIME_UNIT_IN_MILLIS, id -> {
            vertx.eventBus().publish(CLOCK_EVENT_ADDRESS, GSON.toJson(getCurrentDateAndTime(), DateAndTimePOJO.class));
        });

    }

    private DateAndTimePOJO getCurrentDateAndTime() {
        String str_date = ZonedDateTime.now().format(dateFormatter);
        String str_time = ZonedDateTime.now().format(timeFormatter);

        return new DateAndTimePOJO(str_date, str_time);
    }
}
