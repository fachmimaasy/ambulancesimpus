package com.sindu.ambulance.module;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.sindu.ambulance.R;
import com.sindu.ambulance.request.RequestTindakan;
import com.sindu.ambulance.request.RequestTrackingLatLong;
import com.sindu.ambulance.utils.API;
import com.sindu.ambulance.utils.Interface;
import com.sindu.ambulance.utils.NetworkCall;
import com.sindu.ambulance.utils.Session;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by palapabeta on 13/12/17.
 */

public class ActivityOnSite extends AppCompatActivity {

    Button btnnext;
    EditText edttindakan;
    ImageView img1, img2, img3, img4;

    Callback<ResponseBody> cBack;
    private long timer = 60000;
    public static double latitude;
    public static double longitude;
    private Session session;

    private String filePath1;
    private static final int PICK_PHOTO = 1958;
    // private static final int PICK_PHOTO = 2500;
    View view;

    private int up = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_onloc);
        view = getWindow().getDecorView().getRootView();

        btnnext = (Button) findViewById(R.id.btn_nextonlog);
        edttindakan = (EditText) findViewById(R.id.edt_tindakanloc);
        img1 = (ImageView) findViewById(R.id.img1);
        img2 = (ImageView) findViewById(R.id.img2);
        img3 = (ImageView) findViewById(R.id.img3);
        img4 = (ImageView) findViewById(R.id.img4);

        session = new Session(getApplicationContext());
        session.save(API.POSISITION, "3");

        startTimer();
        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(edttindakan.getText().toString())) {
                    stopTimer();
                    NetworkCall.fileUpload(filePath1, session.get(API.APIKEY),"1", edttindakan.getText().toString());

                } else {
                    Toast.makeText(getApplicationContext(), "Data tindakan harus diisi", Toast.LENGTH_LONG).show();
                }

            }
        });

        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                up = 1;
                addPhoto1(view);
            }
        });

        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                up = 2;
                addPhoto1(view);

            }
        });
        img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                up = 3;
                addPhoto1(view);

            }
        });
        img4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                up = 4;
                addPhoto1(view);

            }
        });

    }

    public void addPhoto1(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_PHOTO);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_PHOTO) {
            Uri imageUri = data.getData();
            filePath1 = getPath(imageUri);
            if (up == 1) {
                img1.setImageURI(imageUri);
            } else if (up == 2) {
                img2.setImageURI(imageUri);
            } else if (up == 3) {
                img3.setImageURI(imageUri);
            } else if (up == 4) {
                img4.setImageURI(imageUri);
            }
            Log.i("xxx1122", imageUri + "");
        }
    }

    private String getPath(Uri uri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    private void tindakan() {
        Interface APIInterface = API.initRetrofit(true);
        RequestTindakan.Data data = new RequestTindakan.Data("67", edttindakan.getText().toString());
        RequestTindakan params = new RequestTindakan("0108", data, session.get(API.APIKEY), "b3392645d446ff61bb7b6597d3e7eb98e4866bc6");
        Call<ResponseBody> call = APIInterface.requestTindakan(params);
        cBack = new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    if (json.getString("status").compareTo("200") == 0) {

                        ActivityOnSite.this.finish();
                        startActivity(new Intent(getApplicationContext(), ActivityOtwHospital.class));
                    }else{
                        ActivityOnSite.this.finish();
                        startActivity(new Intent(getApplicationContext(), ActivityOtwHospital.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                API.retryDialog(ActivityOnSite.this, call, cBack, 3, true);
            }
        };
        API.enqueueWithRetry(ActivityOnSite.this, call, cBack, true);
    }

    private void tracking() {
        Interface APIInterface = API.initRetrofit(true);
        RequestTrackingLatLong.Data data = new RequestTrackingLatLong.Data(String.valueOf(latitude), String.valueOf(longitude));
        RequestTrackingLatLong params = new RequestTrackingLatLong("0105", data, session.get(API.APIKEY), "b3392645d446ff61bb7b6597d3e7eb98e4866bc6");
        Call<ResponseBody> call = APIInterface.requestTrackingLatLong(params);
        cBack = new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    if (json.getString("status").compareTo("200") == 0) {

                    } else {
                        Toast.makeText(ActivityOnSite.this, "Username atau password salah", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                API.retryDialog(ActivityOnSite.this, call, cBack, 3, true);
            }
        };
        API.enqueueWithRetry(ActivityOnSite.this, call, cBack, true);
    }


    public void afficher() {
        gpsTracker();
        handler.postDelayed(runnable, timer);
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
        Toast.makeText(getApplicationContext(), "Disable on Exit", Toast.LENGTH_LONG).show();
    }

    @Subscribe
    public void onEvent(String event) {
        if (event == "true") {
            Toast.makeText(getApplicationContext(), "Pengiriman Data Sukses", Toast.LENGTH_LONG).show();
            ActivityOnSite.this.finish();
            startActivity(new Intent(getApplicationContext(), ActivityOtwHospital.class));
        }else{
            Toast.makeText(getApplicationContext(), "Pengiriman Sata Gagal", Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}

