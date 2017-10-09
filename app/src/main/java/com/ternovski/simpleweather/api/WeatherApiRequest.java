package com.ternovski.simpleweather.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import com.ternovski.simpleweather.JSONHandler;
import com.ternovski.simpleweather.R;
import com.ternovski.simpleweather.WeatherConstants;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Vadim on 2017.
 */

public class WeatherApiRequest {

    private final static String DARK_SKY_API = "https://api.darksky.net/forecast/";

    public static void makeRequest(Location currentLocation, Context context, JSONHandler.ParseResult onResult) {
        WeatherRequest weatherRequest = new WeatherRequest(context, onResult);
        weatherRequest.execute(currentLocation);
    }

    private static class WeatherRequest extends AsyncTask<Location, Void, String> {

        private Context context;
        private JSONHandler.ParseResult onResult;

        public WeatherRequest(Context context, JSONHandler.ParseResult onResult) {
            this.context = context;
            this.onResult = onResult;
        }

        @Override
        protected String doInBackground(Location... locations) {
            StringBuilder result = new StringBuilder();
            URL apiUrl;
            HttpURLConnection connection;
            String darkSkyApiKey = context.getResources().getString(R.string.dark_sky_api_key);
            try {
                String longitude = String.valueOf(locations[0].getLongitude());
                String latitude = String.valueOf(locations[0].getLatitude());
                apiUrl = new URL(DARK_SKY_API + darkSkyApiKey + "/" + latitude + "," + longitude);
                connection = (HttpURLConnection) apiUrl.openConnection();
                InputStream inputStream = new BufferedInputStream(connection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null)
                    result.append(line);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String requestResult) {
            new JSONHandler(requestResult, onResult);
            saveLatestWeather(requestResult);
        }

        private void saveLatestWeather(String result) {
            AppCompatActivity appCompatActivity = (AppCompatActivity) context;
            SharedPreferences lastKnownWeather = appCompatActivity.getPreferences(context.MODE_PRIVATE);
            SharedPreferences.Editor editor = lastKnownWeather.edit();
            editor.putString(WeatherConstants.LATEST_SAVED_WEATHER, result);
            editor.apply();
        }
    }
}
