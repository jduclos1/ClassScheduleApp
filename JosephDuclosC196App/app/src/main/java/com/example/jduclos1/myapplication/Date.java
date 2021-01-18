package com.example.jduclos1.myapplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Date {
    public static long getDate(String dateInput) {
        try {
            java.util.Date date = Date.dateFormat.parse(dateInput + " MST");
            return date.getTime();
        } catch (ParseException e) {
            // oh well
            return 0;
        }
    }

    public static long todayLong() {
        String currentDate = Date.dateFormat.format(new java.util.Date());
        return getDate(currentDate);
    }

    public static long todayLongWithTime() {
        return System.currentTimeMillis();
    }

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd z", Locale.getDefault());
    public static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS z", Locale.getDefault());
}
