package com.e.organizer_app;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateHelper {

    // Метод преобразует дату к строке с датой в формате UTC
    public static String convertToUTCDateString(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    // Метод позволяет получить номер текущего дня недели
    public static int getCurrentWeekDayIndex(){
        // экземпляр календаря
        Calendar instance = Calendar.getInstance();
        return instance.get(Calendar.DAY_OF_WEEK);
    }

    // Метод преобразует строку в формате UTC к дате
    public static Date convertUTCStringToDate(String string)  {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = dateFormat.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    // Метод преобразует строку даты к строке с иным паттерном
    public static String convertUTCDateStringToTimeString(String string) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
        Date date = null;
        try {
            date = sdf1.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sdf2.format(date);
    }

    // Метод преобразует строку даты к строке с иным паттерном
    public static String convertUTCDateStringToTSWithWeekDay(String string) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("EEEE HH:mm");
        Date date = null;
        try {
            date = sdf1.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return capitalize(sdf2.format(date));
    }

    // Метод преобразует строку даты к строке с иным паттерном
    public static String convertUTCDateStringToDay(String string) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("EEEE dd MMMM yyyy");
        Date date = null;
        try {
            date = sdf1.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return capitalize(sdf2.format(date));
    }

    // Метод изменяет первую букву строки на заглавную
    private static String capitalize(String str)
    {
        if(str == null) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}
