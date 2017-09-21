package com.matteoveroni.vertxjavafxchatclient.events;

import com.matteoveroni.vertxjavafxchatbusinesslogic.pojos.DateAndTimePOJO;

public class EventClockUpdate {

    private final DateAndTimePOJO dateAndTime;

    public EventClockUpdate(DateAndTimePOJO dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public DateAndTimePOJO getDateAndTime() {
        return dateAndTime;
    }
}
