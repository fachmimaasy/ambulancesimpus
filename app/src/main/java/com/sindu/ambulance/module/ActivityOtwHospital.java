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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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
 * Created by dasdassdaterter--- on 12/14/2017.
 */

public class ActivityOtwHospital extends AppCompatActivity implements OnMapReadyCallback {

    public static double latitude;
    public static double longitude;
    Callback<ResponseBody> cBack;
    private String idKejadian;
    private long timer= 10000;
    Session session;
    Button sampai;
    private GoogleMap mMap;
    private MapView mapView;
    String markermap;
    TextView status, posisi, driver, plat;
    LinearLayout linkejadian;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_fragment);

        session = new Session(getApplicationContext());
        session.save(API.POSISITION,"4");
        startTimer();
        status = (TextView) findViewById(R.id.edt_status);
        posisi = (TextView) findViewById(R.id.edt_posisi);
        driver = (TextView) findViewById(R.id.edt_driver);
        plat   = (TextView) findViewById(R.id.edt_plat);
        linkejadian = (LinearLayout)findViewById(R.id.lin_datakej) ;
        sampai = (Button) findViewById(R.id.btn_berangkat);

        linkejadian.setVisibility(View.GONE);
        sampai.setText("Sampai lokasi & buat laporan");
        sampai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTimer();
                ActivityOtwHospital.this.finish();
                startActivity(new Intent(getApplicationContext(), ActivityOnHospital.class));
            }
        });

        try {

            JSONObject jsonUser = new JSONObject(session.get(API.USER));
            JSONObject jsonAmbulance = new JSONObject(session.get(API.AMBULANCE));
            posisi.setText((jsonUser.getString("rs_name")));
            driver.setText(jsonUser.getString("username"));
            status.setText(jsonAmbulance.getString("title"));
            plat.setText(jsonAmbulance.getString("plat"));



        } catch (JSONException e) {
            e.printStackTrace();
        }



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        Button stop = (Button) findViewById(R.id.btn_stoptimer);
//        stop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                stopTimer();
//                ActivityOtwHospital.this.finish();
//                startActivity(new Intent(getApplicationContext(),ActivityOnHospital.class));
//            }
//        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        float zoomLevel = 16.0f; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoomLevel));
    }

    private void tracking() {
        Interface APIInterface = API.initRetrofit(true);
        RequestTrackingLatLong.Data data = new RequestTrackingLatLong.Data(String.valueOf(latitude),String.valueOf(longitude));
        RequestTrackingLatLong params = new RequestTrackingLatLong("0105", data, session.get(API.APIKEY),"b3392645d446ff61bb7b6597d3e7eb98e4866bc6");
        Call<ResponseBody> call = APIInterface.requestTrackingLatLong(params);
        cBack = new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    if (json.getString("status").compareTo("200") == 0) {

                    } else {
                        Toast.makeText(ActivityOtwHospital.this, "Username atau password salah", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                API.retryDialog(ActivityOtwHospital.this, call, cBack, 3, false);
            }
        };
        API.enqueueWithRetry(ActivityOtwHospital.this, call, cBack, false);
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
        tracking();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(),"Disable on Exit",Toast.LENGTH_LONG).show();
    }

}
