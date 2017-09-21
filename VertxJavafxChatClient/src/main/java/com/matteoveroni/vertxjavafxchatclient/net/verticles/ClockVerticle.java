package com.matteoveroni.vertxjavafxchatclient.net.verticles;

import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.DateAndTimePOJO;
import com.matteoveroni.vertxjavafxchatclient.events.EventClockUpdate;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.greenrobot.eventbus.EventBus;

public class ClockVerticle extends AbstractVerticle {

    public static final String BUS_ADDRESS_TIMER = "timeraddress";

    private static final int CLOCK_TIME_UNIT_IN_MILLIS = 1000;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    private static final EventBus SYSTEM_EVENT_BUS = EventBus.getDefault();

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertx.setPeriodic(CLOCK_TIME_UNIT_IN_MILLIS, id -> {

            SYSTEM_EVENT_BUS.postSticky(new EventClockUpdate(getCurrentDateAndTime()));

        });

    }

    private DateAndTimePOJO getCurrentDateAndTime() {
        String str_date = ZonedDateTime.now().format(dateFormatter);
        String str_time = ZonedDateTime.now().format(timeFormatter);

        return new DateAndTimePOJO(str_date, str_time);
    }
}
