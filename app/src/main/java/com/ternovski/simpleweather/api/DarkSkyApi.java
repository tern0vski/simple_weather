package com.ternovski.simpleweather.api;

import com.ternovski.simpleweather.model.WeatherModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Vadim on 2017.
 */

public interface DarkSkyApi {

    @GET("forecast/{key}/{coordinates}")
    Call<WeatherModel> getData(@Path("key") String key, @Path("coordinates") String coordinates);
}
