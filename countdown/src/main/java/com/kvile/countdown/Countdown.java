package com.kvile.countdown;

import java.time.temporal.ChronoUnit;
import java.util.Calendar;

public class Countdown {

    private long id;
    private String name;
    private Calendar calendar;

    public Countdown(long id, String name, Calendar calendar) {
        this.id = id;
        this.name = name;
        this.calendar = calendar;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public long getDaysRemaining() {
        return ChronoUnit.DAYS.between(Calendar.getInstance().toInstant(), getCalendar().toInstant());
    }

}
