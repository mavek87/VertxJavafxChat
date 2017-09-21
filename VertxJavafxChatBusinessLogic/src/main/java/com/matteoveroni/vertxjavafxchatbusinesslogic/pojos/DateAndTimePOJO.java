package com.matteoveroni.vertxjavafxchatbusinesslogic.pojos;

public class DateAndTimePOJO {

    private final String date;
    private final String time;

    public DateAndTimePOJO(String date, String time) {
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
