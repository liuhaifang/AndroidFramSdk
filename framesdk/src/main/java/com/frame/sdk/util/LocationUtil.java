package com.frame.sdk.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.io.IOException;
import java.util.List;


public class LocationUtil {
    private static LocationManager locationManager;
    private static LocationListener locationListener;
    private static Geocoder geocoder;

    /**
     * 返回 同步获取的最后一次位置数据，可能会有缓存
     */
    public static Location getCurrLocation(Context ctx, final GetCurrLocationListener getCurrLocationListener) {
        locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        //获取所有可用的位置提供器
        List<String> providers = locationManager.getProviders(true);
        String locationProvider = null;
        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.PASSIVE_PROVIDER)) {
            //如果是PASSIVE_PROVIDER
            locationProvider = LocationManager.PASSIVE_PROVIDER;
        } else {
            LogUtils.e("没有可用的位置提供器");
            if (getCurrLocationListener != null) {
                getCurrLocationListener.error("没有可用的位置提供器");
            }
            return null;
        }

        Location location = locationManager.getLastKnownLocation(locationProvider);
        if (location != null) {
            if (getCurrLocationListener != null) {
                getCurrLocationListener.lastKnownLocation(location);
            }
            return location;
        }

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LogUtils.d("onLocationChanged location==" + location);
                if (getCurrLocationListener != null)
                    getCurrLocationListener.updateLocation(location);
                locationManager.removeUpdates(locationListener);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                LogUtils.d("onStatusChanged status==" + status);
            }

            @Override
            public void onProviderEnabled(String provider) {
                LogUtils.d("onProviderEnabled provider==" + provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                LogUtils.d("onProviderDisabled provider==" + provider);
            }
        };

        locationManager.requestLocationUpdates(locationProvider, 10, 0.001f, locationListener);
        return location;
    }

    public static String getCity(Context context, Location location) {
        String city = "";
        if (location == null) {
            return city;
        }
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        LogUtils.i("lat=" + lat + ",lng=" + lng);
        if (geocoder == null)
            geocoder = new Geocoder(context);
        try {
            List<Address> addList = geocoder.getFromLocation(lat, lng, 1);
            if (addList != null && addList.size() > 0) {
                city = addList.get(0).getLocality();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return city;
    }


    public interface GetCurrLocationListener {
        void error(String errMsg);

        /*
        通过监听位置更新获得的最新更新位置，异步
         */
        void updateLocation(Location location);

        /*
        同步获取的最后一次位置数据，可能会有缓存
         */
        void lastKnownLocation(Location location);
    }
}
