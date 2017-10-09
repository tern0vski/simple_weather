package com.ternovski.simpleweather.utils;

/**
 * Created by Vadim on 2017.
 */

public class Converter {

    public static double convertCelsiusToFahrenheit(double fahrenheitTemp) {
        return ((fahrenheitTemp - 32.0) * (5.0 / 9.0));
    }

    public static double convertKilometersToMiles(double miles){
        return miles * 1.6;
    }
}
