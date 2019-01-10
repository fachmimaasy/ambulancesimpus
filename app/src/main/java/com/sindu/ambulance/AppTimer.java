package com.sindu.ambulance;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.sindu.ambulance.request.RequestTrackingTwo;
import com.sindu.ambulance.utils.API;
import com.sindu.ambulance.utils.Interface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by palapabeta on 11/12/17.
 */

public class AppTimer extends Application {

    public static AppTimer appInstance;
    private SimpleDateFormat dateFormat;
    public static double latitude;
    public static double longitude;
    Callback<ResponseBody> cBack;
    private String idKejadian;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate() {
        super.onCreate();

        appInstance = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        }
    }

    public void afficher() {
        gpsTracker();
        //handler.postDelayed(runnable,600000);
        handler.postDelayed(runnable,20000);
    }

    public void startTimer() {
        runnable.run();
    }

    public void stopTimer() {
        handler.removeCallbacks(runnable);
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        public void run() {
            afficher();
        }
    };

    private void gpsTracker() {
        LocationManager lm = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {
            locationbyGPS(location);
        }

        final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                locationbyGPS(location);
            }

            public void onProviderDisabled(String arg0) {


            }

            public void onProviderEnabled(String arg0) {


            }

            public void onStatusChanged(String arg0, int arg1, Bundle arg2) {


            }
        };

       // lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
    }

    private void locationbyGPS(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.i("LAT+++", latitude + "");
        Log.i("LONG+++", longitude + "");

    }




}

