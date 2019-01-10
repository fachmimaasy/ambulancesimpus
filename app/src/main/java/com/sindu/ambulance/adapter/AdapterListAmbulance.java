package com.sindu.ambulance.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sindu.ambulance.R;
import com.sindu.ambulance.model.ModelListAmbulance;
import com.sindu.ambulance.module.ActivityHome;
import com.sindu.ambulance.module.ActivityLogin;
import com.sindu.ambulance.request.RequestChangeAmbulance;
import com.sindu.ambulance.utils.API;
import com.sindu.ambulance.utils.Interface;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by dasdassdaterter--- on 12/17/2017.
 */

public class AdapterListAmbulance extends RecyclerView.Adapter<AdapterListAmbulance.MyViewHolder> {

    private Context mContext;
    private List<ModelListAmbulance> ambulancesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView plat;
        public LinearLayout lineadplist;

        public MyViewHolder(View view) {
            super(view);
            plat = (TextView) view.findViewById(R.id.txt_listplat);
            lineadplist = (LinearLayout) view.findViewById(R.id.lineadplist);
        }
    }

    public AdapterListAmbulance(Context mContext, List<ModelListAmbulance> albumList) {
        this.mContext = mContext;
        this.ambulancesList = albumList;
    }

    @Override
    public AdapterListAmbulance.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_list, parent, false);

        return new AdapterListAmbulance.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AdapterListAmbulance.MyViewHolder holder, final int position) {
        final ModelListAmbulance ambulance = ambulancesList.get(position);
        holder.plat.setText(ambulance.getPlat());


        holder.lineadplist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ActivityLogin act = new ActivityLogin();
                //Log.i("CEKADAPTER",ambulance.getPlat());
              //  act.changeAmbulance(ambulance.getId());
                ambulance.setId(ambulance.getId());
                EventBus.getDefault().post(ambulance.getId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return ambulancesList.size();
    }
}


