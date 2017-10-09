package com.ternovski.simpleweather.utils;

/**
 * Created by Vadim on 2017.
 */

public class Utils {

    public static long findSeekBarPosition(long sunrise, long sunset) {
        long position = 0;
        try {
            long currentSeconds = System.currentTimeMillis() / 1000L;
            long difference = DateFormatUtil.countDifferenceBetweenTwoTimeStamps(sunrise, sunset);
            long diffBetweenCurrentTimeAndSunrise = DateFormatUtil.countDifferenceBetweenTwoTimeStamps(sunrise, currentSeconds);
            position = (100 * diffBetweenCurrentTimeAndSunrise) / difference;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return position;
    }

    public static void isNight(){

    }
}
