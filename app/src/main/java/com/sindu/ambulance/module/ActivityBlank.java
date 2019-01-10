package com.sindu.ambulance.module;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.sindu.ambulance.R;
import com.sindu.ambulance.utils.API;
import com.sindu.ambulance.utils.Session;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by palapabeta on 26/12/17.
 */

public class ActivityBlank extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank);
        Session session = new Session(ActivityBlank.this);
        try {
            API.APIKEY = session.get(API.APIKEY);

            if (session.get(API.POSISITION).equals("1")) {
                ActivityBlank.this.finish();
                startActivity(new Intent(ActivityBlank.this, ActivityHome.class));
            } else if (session.get(API.POSISITION).equals("2")) {
                ActivityBlank.this.finish();
                startActivity(new Intent(ActivityBlank.this, ActivityOtwKejadian.class));
            } else if (session.get(API.POSISITION).equals("3")) {
                ActivityBlank.this.finish();
                startActivity(new Intent(ActivityBlank.this, ActivityOnSite.class));
            } else if (session.get(API.POSISITION).equals("4")) {
                ActivityBlank.this.finish();
                startActivity(new Intent(ActivityBlank.this, ActivityOtwHospital.class));
            } else if (session.get(API.POSISITION).equals("5")) {
                ActivityBlank.this.finish();
                startActivity(new Intent(ActivityBlank.this, ActivityOnHospital.class));
            } else if (session.get(API.POSISITION).equals("6")) {
                ActivityBlank.this.finish();
                startActivity(new Intent(ActivityBlank.this, ActivityOtwHome.class));
            } else {
                ActivityBlank.this.finish();
                startActivity(new Intent(ActivityBlank.this, ActivityLogin.class));
            }

        } catch (Exception e){
            e.printStackTrace();
            ActivityBlank.this.finish();
            startActivity(new Intent(ActivityBlank.this, ActivityLogin.class));
        }
    }
}
