package com.ternovski.simpleweather.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

/**
 * Created by Vadim on 2017.
 */

public class LocationUtil {

    private Context mContext;
    private CurrentLocationListener mCurrentLocationListener;
    private Location mLastKnownLocation;

    public LocationUtil(Context mContext, CurrentLocationListener mCurrentLocationListener) {
        this.mContext = mContext;
        this.mCurrentLocationListener = mCurrentLocationListener;
    }

    public void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mLastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            mCurrentLocationListener.currentLocation(mLastKnownLocation);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 200000, 0, mLocationListener);
        } else
            mCurrentLocationListener.onError("GPS not enable");

    }

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            int distance = (int) mLastKnownLocation.distanceTo(location);
            if (distance > 1000 && location != null)
                mCurrentLocationListener.currentLocation(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    public abstract static class CurrentLocationListener {
        public void currentLocation(Location location) {
        }

        public void onError(String error) {
        }
    }
}
