package com.e.organizer_app;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    public static Date addMinutes(Date date, int minutes)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, minutes);
        return cal.getTime();
    }

    public static Date addHours(Date date, int minutes)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, minutes);
        return cal.getTime();
    }

    public static Date addMilliseconds(Date date, long milliseconds)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime() + milliseconds);
        return cal.getTime();
    }
}
