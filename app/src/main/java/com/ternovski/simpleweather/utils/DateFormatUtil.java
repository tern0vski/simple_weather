package com.ternovski.simpleweather.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Vadim on 2017.
 */

public class DateFormatUtil {

    public static String getDateFormat(String format) {
        long unixSeconds = System.currentTimeMillis() / 1000L;
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.ENGLISH);
        return df.format(new Date(unixSeconds * 1000));
    }

    public static String convertUnixTime(String format, long millis) {
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.ENGLISH);
        return df.format(new Date(millis * 1000));
    }

    public static long countDifferenceBetweenTwoTimeStamps(long firstTime, long secondTime){
        long difference = secondTime - firstTime;
        TimeUnit.MILLISECONDS.toMinutes(difference);
        return difference;
    }
}
