package com.ternovski.simpleweather;

import com.ternovski.simpleweather.utils.Converter;
import com.ternovski.simpleweather.utils.DateFormatUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Vadim on 2017.
 */

public class JSONHandler {

    private JSONObject weatherJson;
    private final static String DATE_FORMAT_HH_MM = "kk:mm";
    private final static String DATE_FORMAT_DAY = "EEE";
    private final static String DATE_FORMAT_D = "d";

    public JSONHandler(String result, ParseResult onResult) {
        try {
            this.weatherJson = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        onResult.onResult(this);
    }

    public HashMap<String, Object> getCurrentWeather() {
        HashMap<String, Object> currentWeather = new HashMap<>();
        try {
            JSONObject weatherCurrently = weatherJson.getJSONObject(WeatherConstants.CURRENTLY);

            double currentTemperature = weatherCurrently.getDouble(WeatherConstants.TEMPERATURE);
            int currentTemperatureInt = (int) Converter.convertCelsiusToFahrenheit(currentTemperature);
            currentWeather.put(WeatherConstants.TEMPERATURE, String.valueOf(currentTemperatureInt));

            currentWeather.put(WeatherConstants.SUMMARY, weatherCurrently.getString(WeatherConstants.SUMMARY));

            currentWeather.put(WeatherConstants.ICON, weatherCurrently.getString(WeatherConstants.ICON));

            double windSpeed = weatherCurrently.getDouble(WeatherConstants.WIND_SPEED);
            int windSpeedInt = (int) Converter.convertKilometersToMiles(windSpeed);
            currentWeather.put(WeatherConstants.WIND_SPEED, String.valueOf(windSpeedInt));

            JSONObject weatherDaily = weatherJson.getJSONObject(WeatherConstants.DAILY);
            JSONArray weatherDataDaily = weatherDaily.getJSONArray(WeatherConstants.DATA);

            double maxTemperature = weatherDataDaily.getJSONObject(0).getDouble(WeatherConstants.TEMPERATURE_MAX);
            int maxTemperatureInt = (int) Converter.convertCelsiusToFahrenheit(maxTemperature);

            double minTemperature = (int) weatherDataDaily.getJSONObject(0).getDouble(WeatherConstants.TEMPERATURE_MIN);
            int minTemperatureInt = (int) Converter.convertCelsiusToFahrenheit(minTemperature);

            currentWeather.put(WeatherConstants.TEMPERATURE_MAX, String.valueOf(maxTemperatureInt));
            currentWeather.put(WeatherConstants.TEMPERATURE_MIN, String.valueOf(minTemperatureInt));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentWeather;
    }

    public ArrayList<HashMap<String, Object>> getHourlyWeather() {
        ArrayList<HashMap<String, Object>> hourlyWeatherArray = new ArrayList<>(8);
        HashMap<String, Object> hourlyWeather;
        try {
            JSONObject weatherHourly = weatherJson.getJSONObject(WeatherConstants.HOURLY);
            JSONArray weatherData = weatherHourly.getJSONArray(WeatherConstants.DATA);
            for (int i = 1; i <= 8; i++) {
                hourlyWeather = new HashMap<>();
                double temperatureFahrenheit = weatherData.getJSONObject(i).getDouble(WeatherConstants.TEMPERATURE);
                hourlyWeather.put(WeatherConstants.TEMPERATURE, String.valueOf(convertTemperature(temperatureFahrenheit)));
                hourlyWeather.put(WeatherConstants.ICON, weatherData.getJSONObject(i).getString(WeatherConstants.ICON));
                String convertedTime = DateFormatUtil.convertUnixTime(DATE_FORMAT_HH_MM, (long) weatherData.getJSONObject(i).getInt(WeatherConstants.TIME));
                hourlyWeather.put(WeatherConstants.TIME, convertedTime);
                hourlyWeatherArray.add(hourlyWeather);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return hourlyWeatherArray;
    }

    public HashMap<String, Object> getDailyWeather() {
        ArrayList<String> daysArray = new ArrayList<>(7);
        ArrayList<Integer> temperatureArrayMax = new ArrayList<>(7);
        ArrayList<Integer> temperatureArrayMin = new ArrayList<>(7);
        HashMap<String, Object> dailyWeather = new HashMap<>();

        try {
            JSONObject weatherHourly = weatherJson.getJSONObject(WeatherConstants.DAILY);
            JSONArray weatherData = weatherHourly.getJSONArray(WeatherConstants.DATA);
            for (int i = 1; i <= 7; i++) {
                double temperatureFahrenheitMax = weatherData.getJSONObject(i).getDouble(WeatherConstants.TEMPERATURE_MAX);
                double temperatureFahrenheitMin = weatherData.getJSONObject(i).getDouble(WeatherConstants.TEMPERATURE_MIN);
                temperatureArrayMax.add(convertTemperature(temperatureFahrenheitMax));
                temperatureArrayMin.add(convertTemperature(temperatureFahrenheitMin));
                daysArray.add(DateFormatUtil.convertUnixTime(DATE_FORMAT_DAY, weatherData.getJSONObject(i).getInt(WeatherConstants.TIME)) + ", " + DateFormatUtil.convertUnixTime(DATE_FORMAT_D, (long) weatherData.getJSONObject(i).getInt(WeatherConstants.TIME)));
            }

            long currentMillis = System.currentTimeMillis() / 1000L;
            if (currentMillis < weatherData.getJSONObject(0).getLong(WeatherConstants.SUNSET) && currentMillis > weatherData.getJSONObject(0).getLong(WeatherConstants.SUNRISE))
                dailyWeather.put("daytime", "day");
            else
                dailyWeather.put("daytime", "night");

            dailyWeather.put(WeatherConstants.SUNSET, weatherData.getJSONObject(0).getLong(WeatherConstants.SUNSET));
            dailyWeather.put(WeatherConstants.SUNRISE, weatherData.getJSONObject(0).getLong(WeatherConstants.SUNRISE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dailyWeather.put("days", daysArray);
        dailyWeather.put("temperature_max", temperatureArrayMax);
        dailyWeather.put("temperature_min", temperatureArrayMin);
        return dailyWeather;
    }

    private int convertTemperature(double hourTempCelsiusFahrenheit) throws JSONException {
        int hourTempCelsius = (int) Converter.convertCelsiusToFahrenheit(hourTempCelsiusFahrenheit);
        return hourTempCelsius;
    }

    public abstract static class ParseResult {
        public void onResult(JSONHandler jsonHandler) {
        }

        public void onError() {

        }
    }
}
