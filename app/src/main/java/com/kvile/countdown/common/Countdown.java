package com.kvile.countdown.common;

import java.time.temporal.ChronoUnit;
import java.util.Calendar;

public class Countdown {

    private String name;
    private Calendar calendar;

    public Countdown(String name, Calendar calendar) {
        this.name = name;
        this.calendar = calendar;
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
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_MONTH, -1);
        return ChronoUnit.DAYS.between(now.toInstant(), getCalendar().toInstant());
    }

}
