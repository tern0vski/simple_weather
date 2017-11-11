package com.ternovski.simpleweather.activities;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ternovski.simpleweather.AppBarStateChangeListener;
import com.ternovski.simpleweather.JSONHandler;
import com.ternovski.simpleweather.LocationFinder;
import com.ternovski.simpleweather.R;
import com.ternovski.simpleweather.WeatherConstants;
import com.ternovski.simpleweather.adapters.RecycleViewAdapter;
import com.ternovski.simpleweather.api.WeatherApiRequest;
import com.ternovski.simpleweather.utils.DateFormatUtil;
import com.ternovski.simpleweather.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import im.dacer.androidcharts.LineView;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.current_temperature_text_view)
    TextView mCurrentTemperature;
    @BindView(R.id.current_weather_icon)
    ImageView mCurrentWeatherIcon;
    @BindView(R.id.current_temperature_sign)
    TextView mCurrentTemperatureSign;
    @BindView(R.id.app_bar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.current_weather_background_image)
    ImageView mCurrentWeatherBackgroundImage;
    @BindView(R.id.current_day_min_temp)
    TextView mCurrentDayMinTemperature;
    @BindView(R.id.current_day_max_temp)
    TextView mCurrentDayMaxTemperature;
    @BindView(R.id.current_weather_summary)
    TextView mCurrentWeatherSummary;
    @BindView(R.id.current_wind_speed)
    TextView mCurrentWindSpeed;
    @BindView(R.id.hourly_recycle)
    RecyclerView mHourlyWeatherRecycle;
    @BindView(R.id.layout_toolbar_weather)
    LinearLayout mToolbarWeatherLayout;
    @BindView(R.id.custom_toolbar)
    Toolbar mCustomToolbar;
    @BindView(R.id.current_day)
    TextView mCurrentDay;
    @BindView(R.id.toolbar_current_weather_icon)
    ImageView mCurrentWeatherIconToolbar;
    @BindView(R.id.toolbar_current_temperature)
    TextView mCurrentTemperatureToolbar;
    @BindView(R.id.weather_chart_view)
    LineView mWeatherChart;

    @BindView(R.id.sun_seekbar)
    SeekBar mSunSeekbar;
    @BindView(R.id.sunrise_time)
    TextView mSunriseTime;
    @BindView(R.id.sunset_time)
    TextView mSunsetTime;
    @BindView(R.id.sunPosition1)
    TextView mSunPosition1;
    @BindView(R.id.sunPosition2)
    TextView mSunPosition2;
    @BindView(R.id.sunrise_icon)
    ImageView mSunriseIcon;

    private static final String DATE_FORMAT = "EEEE";
    private static final String DIGIT_SYMBOL = "°";
    private final static String DATE_FORMAT_HH_MM = "kk:mm";

    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private JSONHandler mJsonHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = this;
        mSharedPreferences = getPreferences(MODE_PRIVATE);
        if (getSupportActionBar() != null)
            setSupportActionBar(mCustomToolbar);

        setUI();

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    private void setUI() {
        ViewTreeObserver vto = mSunSeekbar.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {

                try {
                    Resources res = getResources();
                    Drawable thumb = res.getDrawable(R.drawable.sun);
                    int h = 100;//(mSunSeekbar.getMeasuredHeight() * 2); // 8 * 1.5 = 12
                    int w = h;
                    Bitmap bmpOrg = ((BitmapDrawable) thumb).getBitmap();
                    Bitmap bmpScaled = Bitmap.createScaledBitmap(bmpOrg, w, h, true);
                    Drawable newThumb = new BitmapDrawable(res, bmpScaled);
                    newThumb.setBounds(0, 0, newThumb.getIntrinsicWidth(), newThumb.getIntrinsicHeight());
                    mSunSeekbar.setThumb(newThumb);

                    mSunSeekbar.getViewTreeObserver().removeOnPreDrawListener(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            }
        });

        mSunSeekbar.setMax(100);

        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout mAppBarLayout, State state) {
                if (mToolbarWeatherLayout != null) {
                    if (state == State.COLLAPSED) {
                        mToolbarWeatherLayout.setVisibility(View.VISIBLE);
                    } else {
                        mToolbarWeatherLayout.setVisibility(View.GONE);
                    }
                }
            }
        });

        mHourlyWeatherRecycle.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mHourlyWeatherRecycle.setLayoutManager(layoutManager);

        if (mSharedPreferences.contains(WeatherConstants.LATEST_SAVED_WEATHER)) {
            String latestWeather = mSharedPreferences.getString(WeatherConstants.LATEST_SAVED_WEATHER, "");
            new JSONHandler(latestWeather, parseResult);
        }
        mCurrentDay.setText(DateFormatUtil.getDateFormat(DATE_FORMAT));
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            LocationFinder locationFinder = new LocationFinder(this, currentLocationListener);
            locationFinder.getCurrentLocation();
        }
    }

    LocationFinder.CurrentLocationListener currentLocationListener = new LocationFinder.CurrentLocationListener() {
        @Override
        public void currentLocation(Location location) {
            WeatherApiRequest.makeRequest(location, mContext, parseResult);
        }

        @Override
        public void onError(String error) {
            Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
        }
    };

    JSONHandler.ParseResult parseResult = new JSONHandler.ParseResult() {
        @Override
        public void onResult(JSONHandler jsonHandler) {
            super.onResult(jsonHandler);
            mJsonHandler = jsonHandler;
            updateUI();
        }

        @Override
        public void onError() {
            Toast.makeText(mContext, "Unable to update data", Toast.LENGTH_SHORT).show();
        }
    };

    private void updateUI() {
        HashMap<String, Object> result = mJsonHandler.getCurrentWeather();
        String currentTemperature = String.valueOf(result.get(WeatherConstants.TEMPERATURE));
        HashMap<String, Object> hourlyWeather = mJsonHandler.getDailyWeather();

        try {
            if (Integer.valueOf(currentTemperature) < 0) {
                mCurrentTemperatureSign.setText("-");
                currentTemperature = String.valueOf(Integer.valueOf(currentTemperature) * -1);
            } else if (Integer.valueOf(currentTemperature) > 0)
                mCurrentTemperatureSign.setText("+");
            else mCurrentTemperatureSign.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }

        setBackgroundImage(String.valueOf(result.get(WeatherConstants.ICON)));

        currentTemperature = currentTemperature + DIGIT_SYMBOL;
        mCurrentTemperature.setText(currentTemperature);
        mCurrentTemperatureToolbar.setText(currentTemperature);
        mCurrentWeatherSummary.setText(String.valueOf(result.get(WeatherConstants.SUMMARY)));

        mCurrentDayMinTemperature.setText("min :" + " " + String.valueOf(result.get(WeatherConstants.TEMPERATURE_MIN)) + DIGIT_SYMBOL);
        mCurrentDayMaxTemperature.setText("max :" + " " + String.valueOf(result.get(WeatherConstants.TEMPERATURE_MAX)) + DIGIT_SYMBOL);
        mCurrentWindSpeed.setText("wind " + String.valueOf(result.get(WeatherConstants.WIND_SPEED)) + " k/h");

        RecycleViewAdapter recycleViewAdapter = new RecycleViewAdapter(mJsonHandler.getHourlyWeather(), mContext);
        mHourlyWeatherRecycle.setAdapter(recycleViewAdapter);

        makeChart(hourlyWeather);

        if (hourlyWeather.get("daytime").equals("night")) {
            mSunSeekbar.setVisibility(View.GONE);
            mSunsetTime.setVisibility(View.GONE);
            mSunPosition2.setVisibility(View.GONE);
            mSunriseIcon.setVisibility(View.VISIBLE);
        } else {
            mSunSeekbar.setVisibility(View.VISIBLE);
            mSunsetTime.setVisibility(View.VISIBLE);
            mSunPosition2.setVisibility(View.VISIBLE);
            mSunriseIcon.setVisibility(View.GONE);
        }

        int position = (int) Utils.findSeekBarPosition((long) hourlyWeather.get(WeatherConstants.SUNRISE), (long) hourlyWeather.get(WeatherConstants.SUNSET));
        mSunSeekbar.setEnabled(false);
        mSunSeekbar.setProgress(position);
        mSunriseTime.setText(DateFormatUtil.convertUnixTime(DATE_FORMAT_HH_MM, (Long) hourlyWeather.get(WeatherConstants.SUNRISE)));
        mSunsetTime.setText(DateFormatUtil.convertUnixTime(DATE_FORMAT_HH_MM, (Long) hourlyWeather.get(WeatherConstants.SUNSET)));
    }

    private void setBackgroundImage(String weatherIcon) {
        if (weatherIcon.equals("clear-day")) {
            mCurrentWeatherBackgroundImage.setImageDrawable(getResources().getDrawable(R.drawable.sunny));
            mCurrentWeatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.sun));
            mCurrentWeatherIconToolbar.setImageDrawable(getResources().getDrawable(R.drawable.sun));
        } else if (weatherIcon.equals("clear-night")) {
            try {
                mCurrentWeatherBackgroundImage.setImageDrawable(getResources().getDrawable(R.drawable.clear_night));
                mCurrentWeatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.sun));
                mCurrentWeatherIconToolbar.setImageDrawable(getResources().getDrawable(R.drawable.sun));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (weatherIcon.equals("rain") || weatherIcon.equals("snow") || weatherIcon.equals("sleet")) {
            mCurrentWeatherBackgroundImage.setImageDrawable(getResources().getDrawable(R.drawable.rainy));
            mCurrentWeatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.rain));
            mCurrentWeatherIconToolbar.setImageDrawable(getResources().getDrawable(R.drawable.rain));
        } else if (weatherIcon.equals("wind") || weatherIcon.equals("fog") || weatherIcon.equals("cloudy") || weatherIcon.equals("partly-cloudy-night")) {
            mCurrentWeatherBackgroundImage.setImageDrawable(getResources().getDrawable(R.drawable.cloudy));
            mCurrentWeatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.cloudy_2));
            mCurrentWeatherIconToolbar.setImageDrawable(getResources().getDrawable(R.drawable.cloudy_2));
        } else if (weatherIcon.equals("partly-cloudy-day")) {
            mCurrentWeatherBackgroundImage.setImageDrawable(getResources().getDrawable(R.drawable.partly_cloudy));
            mCurrentWeatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.clounds_with_sun));
            mCurrentWeatherIconToolbar.setImageDrawable(getResources().getDrawable(R.drawable.clounds_with_sun));
        }
    }

    public void makeChart(HashMap<String, Object> dailyWeather) {
        mWeatherChart.setDrawDotLine(false);
        mWeatherChart.setShowPopup(LineView.SHOW_POPUPS_All);
        mWeatherChart.setBottomTextList((ArrayList<String>) dailyWeather.get("days"));
        mWeatherChart.setDrawingCacheBackgroundColor(Color.BLUE);
        mWeatherChart.setColorArray(new int[]{Color.parseColor("#F7D358"), Color.parseColor("#0CABDE")});
        ArrayList<Integer> dailyTempArrMax = (ArrayList<Integer>) dailyWeather.get("temperature_max");
        ArrayList<Integer> dailyTempArrMin = (ArrayList<Integer>) dailyWeather.get("temperature_min");
        ArrayList<ArrayList<Integer>> tempArr = new ArrayList<>();
        tempArr.add(dailyTempArrMax);
        tempArr.add(dailyTempArrMin);
        mWeatherChart.setDataList(tempArr);
    }
}
