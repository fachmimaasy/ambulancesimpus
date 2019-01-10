package com.sindu.ambulance.module;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sindu.ambulance.R;
import com.sindu.ambulance.adapter.AdapterListAmbulance;
import com.sindu.ambulance.model.ModelListAmbulance;
import com.sindu.ambulance.request.RequestChangeAmbulance;
import com.sindu.ambulance.request.RequestKejadian;
import com.sindu.ambulance.request.RequestListAmbulance;
import com.sindu.ambulance.request.RequestLogin;
import com.sindu.ambulance.utils.API;
import com.sindu.ambulance.utils.Interface;
import com.sindu.ambulance.utils.Session;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by palapabeta on 13/12/17.
 */

public class ActivityLogin extends AppCompatActivity {
    Callback<ResponseBody> cBack;
    private EditText edtUsername, edtPassword;
    private Button btnActLogin;
    private String username, password;
    private RecyclerView recycle;
    private LinearLayout lineList, lineLogin;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private boolean sentToSettings = false;
    private SharedPreferences permissionStatus;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    String[] permissionsRequired = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE

    };


    private Session session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        edtUsername = (EditText) findViewById(R.id.edit_username);
        edtPassword = (EditText) findViewById(R.id.edit_password);
        btnActLogin = (Button) findViewById(R.id.btn_login);
        lineLogin = (LinearLayout) findViewById(R.id.lin_login);
        lineList = (LinearLayout) findViewById(R.id.lin_listambulance);
        session = new Session(ActivityLogin.this);

//        edtUsername.setText("pkukra@gmail.com");
//        edtPassword.setText("3313033");
        btnActLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = edtUsername.getText().toString();
                password = edtPassword.getText().toString();
                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                    login();
                } else {
                    Toast.makeText(getApplicationContext(), "Data harus lengkap", Toast.LENGTH_LONG).show();
                }
            }
        });
        listVisible(false);
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setPermission();
        }

       // verifyStoragePermissions(this);


    }

    private void login() {
        Interface APIInterface = API.initRetrofit(true);
        RequestLogin.Data data = new RequestLogin.Data(username, password);
        RequestLogin params = new RequestLogin("0101", data, "b3392645d446ff61bb7b6597d3e7eb98e4866bc6");
        Call<ResponseBody> call = APIInterface.requestLogin(params);
        cBack = new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    Log.i("xxx", json + "");
                    if (json.getString("status").compareTo("200") == 0) {
                        session.save(API.APIKEY, json.getJSONObject("data").getString("apiKey").toString());
                        session.save(API.USER, json.getJSONObject("data").toString());

                        ListAmbulance();
                    } else {
                        Toast.makeText(ActivityLogin.this, "Username atau password salah", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    Toast.makeText(ActivityLogin.this, "Lost Connection", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                API.retryDialog(ActivityLogin.this, call, cBack, 3, true);
            }
        };
        API.enqueueWithRetry(ActivityLogin.this, call, cBack, true);
    }

    private void ListAmbulance() {
        Interface APIInterface = API.initRetrofit(true);
        RequestListAmbulance params = new RequestListAmbulance("0112", session.get(API.APIKEY), "b3392645d446ff61bb7b6597d3e7eb98e4866bc6");
        Call<ResponseBody> call = APIInterface.requestListAmbulance(params);
        cBack = new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    if (json.getString("status").compareTo("200") == 0) {
                        JSONArray data = json.getJSONArray("data");
                        if (data.length() > 0) {
                            setListView(data);
                        } else {
                            Toast.makeText(ActivityLogin.this, "Tidak ada daftar ambulan", Toast.LENGTH_LONG).show();

                        }
                        Log.i("===data===", data + "");

                    } else {
                        Toast.makeText(ActivityLogin.this, json.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                API.retryDialog(ActivityLogin.this, call, cBack, 3, true);
            }
        };
        API.enqueueWithRetry(ActivityLogin.this, call, cBack, true);
    }

    private void setListView(JSONArray data) {

        recycle = (RecyclerView) findViewById(R.id.recycle);
        List<ModelListAmbulance> ambulanceList = new ArrayList<ModelListAmbulance>();
        AdapterListAmbulance ambulanceAdapter = new AdapterListAmbulance(this, ambulanceList);
        LinearLayoutManager mLayout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycle.setLayoutManager(mLayout);
        recycle.setItemAnimator(new DefaultItemAnimator());
        recycle.setAdapter(ambulanceAdapter);

        try {
            for (int i = 0; i < data.length(); i++) {
                ModelListAmbulance mList = new ModelListAmbulance();
                Log.i("===ID===", data.getJSONObject(i).getString("id_ambulan") + "");
                mList.setId(data.getJSONObject(i).getString("id_ambulan"));
                mList.setPlat(data.getJSONObject(i).getString("plat"));
                mList.setLatitude(data.getJSONObject(i).getString("latitude"));
                mList.setLongitude(data.getJSONObject(i).getString("longitude"));
                ambulanceList.add(mList);
            }
            ambulanceAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        listVisible(true);
    }

    public void changeAmbulance(String idAmbulance) {
        Interface APIInterface = API.initRetrofit(true);
        RequestChangeAmbulance.Data data = new RequestChangeAmbulance.Data(idAmbulance);
        RequestChangeAmbulance params = new RequestChangeAmbulance("0113", data, session.get(API.APIKEY), "b3392645d446ff61bb7b6597d3e7eb98e4866bc6");
        Call<ResponseBody> call = APIInterface.requestChangeAmbulance(params);
        cBack = new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    if (json.getString("status").compareTo("200") == 0) {
                        session.save(API.AMBULANCE, json.getJSONObject("data").toString());
                        getKejadian();

                    } else {
                        Toast.makeText(ActivityLogin.this, json.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                API.retryDialog(ActivityLogin.this, call, cBack, 3, true);
            }
        };

        API.enqueueWithRetry(ActivityLogin.this, call, cBack, true);
    }

    private void getKejadian() {
        Interface APIInterface = API.initRetrofit(true);
        RequestKejadian params = new RequestKejadian("0104", session.get(API.APIKEY), "b3392645d446ff61bb7b6597d3e7eb98e4866bc6");
        Call<ResponseBody> call = APIInterface.requestKejadian(params);
        cBack = new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject json = new JSONObject(response.body().string());
                    if (json.getString("status").compareTo("200") == 0) {
                        Log.i("kejadian", json + "");
                        session.save(API.KEJADIAN, json.getJSONObject("data").toString());
                        ActivityLogin.this.finish();
                        startActivity(new Intent(ActivityLogin.this, ActivityHome.class));
                    } else {
                        Toast.makeText(ActivityLogin.this, json.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                API.retryDialog(ActivityLogin.this, call, cBack, 3, false);
            }
        };
        API.enqueueWithRetry(ActivityLogin.this, call, cBack, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);

    }

    @Subscribe
    public void onEvent(String event) {
        changeAmbulance(event);
    }

    private void listVisible(boolean set) {
        if (set) {
            Log.i("===set===", "true");
            lineLogin.setVisibility(View.GONE);
            lineList.setVisibility(View.VISIBLE);
        } else {
            Log.i("===set===", "false");
            lineLogin.setVisibility(View.VISIBLE);
            lineList.setVisibility(View.GONE);
        }

    }

    private void setPermission() {
        if (ActivityCompat.checkSelfPermission(ActivityLogin.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(ActivityLogin.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(ActivityLogin.this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ActivityLogin.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(ActivityLogin.this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(ActivityLogin.this, permissionsRequired[2])) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityLogin.this);
                builder.setTitle("Izin Aplikasi");
                builder.setMessage("Aplikasi ini memerlukan beberapa izin penggunanan");
                builder.setPositiveButton("Mengizinkan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(ActivityLogin.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(permissionsRequired[0], false)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityLogin.this);
                builder.setTitle("Izin Aplikasi");
                builder.setMessage("Aplikasi ini memerlukan beberapa izin penggunanan");
                builder.setPositiveButton("Mengizinkan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Mulai mengizinkan penggunaan", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(ActivityLogin.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }


            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.commit();
        } else {
            //You already have the permission, just go ahead.
            proceedAfterPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if (allgranted) {
                proceedAfterPermission();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(ActivityLogin.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(ActivityLogin.this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(ActivityLogin.this, permissionsRequired[2])) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityLogin.this);
                builder.setTitle("Izin Aplikasi");
                builder.setMessage("Aplikasi ini memerlukan beberapa izin penggunanan");
                builder.setPositiveButton("Mulai mengizinkan penggunaan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(ActivityLogin.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                Toast.makeText(getBaseContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(ActivityLogin.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }

    private void proceedAfterPermission() {
        Log.i("Verif", "Verify Accses");
        //Toast.makeText(getBaseContext(), "Verify Accses", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(ActivityLogin.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


}
