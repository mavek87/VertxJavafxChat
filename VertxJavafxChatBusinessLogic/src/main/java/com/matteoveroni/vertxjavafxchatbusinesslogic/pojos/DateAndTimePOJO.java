package com.matteoveroni.vertxjavafxchatbusinesslogic.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DateAndTimePOJO {

    private final String date;
    private final String time;

    public DateAndTimePOJO(@JsonProperty("date") String date, @JsonProperty("time") String time) {
        this.date = date;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
