package com.sindu.ambulance.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;


import org.greenrobot.eventbus.EventBus;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NetworkCall {

    public static void fileUpload(String filePath, String apikey, String idkejadian,String tindakan) {

        Interface apiInterface = API.initRetrofit(true);

        File file = new File(filePath);
        //create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("image"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("file[]", file.getName(), requestFile);

        Gson gson = new Gson();
       // String patientData = gson.toJson(imageSenderInfo);

        //RequestBody description = RequestBody.create(okhttp3.MultipartBody.FORM, patientData);

        RequestBody apiKey = RequestBody.create(okhttp3.MultipartBody.FORM, apikey);
        RequestBody signature = RequestBody.create(okhttp3.MultipartBody.FORM, "b3392645d446ff61bb7b6597d3e7eb98e4866bc6");
        RequestBody idKejadian = RequestBody.create(okhttp3.MultipartBody.FORM, idkejadian);
        RequestBody tindakan_ket = RequestBody.create(okhttp3.MultipartBody.FORM,  tindakan);

        // finally, execute the request
        Call<ResponseBody> call = apiInterface.fileUpload(apiKey,signature,idKejadian,tindakan_ket, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
               // Log.i("Response: " + response+"");

               // ResponseModel responseModel = response.body();

                if(response.body() != null){
                    EventBus.getDefault().post("true");
                    Log.i("response",response.body()+"");

                } else
                  //  EventBus.getDefault().post(new EventModel("response", "ResponseModel is NULL"));
                Log.i("response","ResponseModel is NULL");
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
//                Logger.d("Exception: " + t);
//                EventBus.getDefault().post(new EventModel("response", t.getMessage()));
                Log.i("responseFailure",t.getMessage());
            }
        });
    }

}
