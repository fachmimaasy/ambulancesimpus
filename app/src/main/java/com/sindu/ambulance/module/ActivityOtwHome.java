package com.sindu.ambulance.module;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sindu.ambulance.R;
import com.sindu.ambulance.request.RequestTrackingLatLong;
import com.sindu.ambulance.utils.API;
import com.sindu.ambulance.utils.Interface;
import com.sindu.ambulance.utils.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by palapabeta on 18/12/17.
 */

public class ActivityOtwHome extends AppCompatActivity {

    public static double latitude;
    public static double longitude;
    Callback<ResponseBody> cBack;
    private String idKejadian;
    private long timer= 30000;
    private Session session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otw);
        TextView edtOtw = (TextView) findViewById(R.id.txt_otw);
        edtOtw.setText("Perjalanan Pulang");
        session = new Session(getApplicationContext());
        session.save(API.POSISITION,"6");

        startTimer();
        Button stop = (Button) findViewById(R.id.btn_stoptimer);
        Button exit = (Button) findViewById(R.id.btn_logout);
        exit.setVisibility(View.VISIBLE);
        stop.setText("Standby Aplikasi");
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tracking("0116");
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTimer();
                ActivityOtwHome.this.finish();
                startActivity(new Intent(getApplicationContext(),ActivityHome.class));
            }
        });
    }

    private void tracking(final String headercode) {
        Interface APIInterface = API.initRetrofit(true);
        RequestTrackingLatLong.Data data = new RequestTrackingLatLong.Data(String.valueOf(latitude),String.valueOf(longitude));
        RequestTrackingLatLong params = new RequestTrackingLatLong(headercode, data, session.get(API.APIKEY),"b3392645d446ff61bb7b6597d3e7eb98e4866bc6");
        Call<ResponseBody> call = APIInterface.requestTrackingLatLong(params);
        cBack = new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    if (json.getString("status").compareTo("200") == 0) {
                        if(headercode.equals("0116")){
                            session.clear();
                            ActivityOtwHome.this.finish();
                            startActivity(new Intent(getApplicationContext(),ActivityLogin.class));

                        }
                    } else {
                        Toast.makeText(ActivityOtwHome.this, "Username atau password salah", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                API.retryDialog(ActivityOtwHome.this, call, cBack, 3, false);
            }
        };
        API.enqueueWithRetry(ActivityOtwHome.this, call, cBack, false);
    }


    public void afficher() {
        gpsTracker();
        handler.postDelayed(runnable,timer);
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
    }

    private void locationbyGPS(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        tracking("0115");
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(),"Disable on Exit",Toast.LENGTH_LONG).show();
    }

}
