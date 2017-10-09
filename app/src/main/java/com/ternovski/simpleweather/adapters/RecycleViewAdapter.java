package com.ternovski.simpleweather.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ternovski.simpleweather.R;
import com.ternovski.simpleweather.utils.DateFormatUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Vadim on 2017.
 */

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {
    private ArrayList<HashMap<String, Object>> mHourlyWeather;
    private Context mContext;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTimeTextView;
        public TextView mTemperatureTextView;
        public ImageView mWeatherByHourIcon;
        public TextView mDay;

        public ViewHolder(View v) {
            super(v);
            mTimeTextView = (TextView) v.findViewById(R.id.time);
            mDay = (TextView) v.findViewById(R.id.day_of_week);
            mTemperatureTextView = (TextView) v.findViewById(R.id.temperature);
            mWeatherByHourIcon = (ImageView) v.findViewById(R.id.hourly_weather_icon);
        }
    }

    public RecycleViewAdapter(ArrayList<HashMap<String, Object>> hourlyWeather, Context mContext) {
        this.mHourlyWeather = hourlyWeather;
        this.mContext = mContext;
    }

    @Override
    public RecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_hourly_weather_recycle_item, parent, false);
        int height = parent.getMeasuredHeight();
        int width = parent.getMeasuredWidth() / 5;
        v.setLayoutParams(new RecyclerView.LayoutParams(width, height));
        //v.setBackgroundColor(Color.parseColor("#2F2F2F"));
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String time = String.valueOf(mHourlyWeather.get(position).get("time"));
        if (position == 0) {
            holder.mDay.setVisibility(View.VISIBLE);
            String currentDate = DateFormatUtil.convertUnixTime("EEE", (System.currentTimeMillis() / 1000L));
            holder.mDay.setText(currentDate);
        } else if (time.equals("24:00")) {
            holder.mDay.setVisibility(View.VISIBLE);
            String currentDate = DateFormatUtil.convertUnixTime("EEE", (System.currentTimeMillis() / 1000L) + (1000 * 60 * 60 * 24));
            holder.mDay.setText(currentDate);
        }

        holder.mTimeTextView.setText(time);
        String dayTemperature = String.valueOf(mHourlyWeather.get(position).get("temperature")) + mContext.getResources().getString(R.string.digit_symbol);
        holder.mTemperatureTextView.setText(dayTemperature);
        setWeatherImage(holder, String.valueOf(mHourlyWeather.get(position).get("icon")));
    }

    private void setWeatherImage(ViewHolder holder, String icon) {
        if (icon.equals("clear-day") || icon.equals("clear-night")) {
            holder.mWeatherByHourIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.sun));
        } else if (icon.equals("rain") || icon.equals("snow") || icon.equals("sleet")) {
            holder.mWeatherByHourIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.rain));
        } else if (icon.equals("wind") || icon.equals("fog") || icon.equals("cloudy") || icon.equals("partly-cloudy-night")) {
            holder.mWeatherByHourIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.cloudy_2));
        } else if (icon.equals("partly-cloudy-day")) {
            holder.mWeatherByHourIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.clounds_with_sun));
        }
    }

    @Override
    public int getItemCount() {
        return mHourlyWeather.size();
    }
}

