package com.sindu.ambulance.utils;


import com.sindu.ambulance.request.RequestChangeAmbulance;
import com.sindu.ambulance.request.RequestEndLoc;
import com.sindu.ambulance.request.RequestKejadian;
import com.sindu.ambulance.request.RequestListAmbulance;
import com.sindu.ambulance.request.RequestLogin;
import com.sindu.ambulance.request.RequestTindakan;
import com.sindu.ambulance.request.RequestTrackingLatLong;
import com.sindu.ambulance.request.RequestTrackingOne;
import com.sindu.ambulance.request.RequestTrackingTwo;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


/**
 * Created by XGibar on 27/10/2016.
 */
public interface Interface {
    @POST(API.URL+"v1")
    Call<ResponseBody> requestLogin(@Body RequestLogin requestLogin);
    @POST(API.URL+"v1")
    Call<ResponseBody> requestListAmbulance(@Body RequestListAmbulance requestListAmbulance);
    @POST(API.URL+"v1")
    Call<ResponseBody> requestTrackingOne(@Body RequestTrackingOne requestTrackingOne);
    @POST(API.URL+"v1")
    Call<ResponseBody> requestTrackingTwo(@Body RequestTrackingTwo requestTrackingTwo);
    @POST(API.URL+"v1")
    Call<ResponseBody> requestTindakan(@Body RequestTindakan requestTindakan);
    @POST(API.URL+"v1")
    Call<ResponseBody> requestEndLoc(@Body RequestEndLoc requestEndLoc);
    @POST(API.URL+"v1")
    Call<ResponseBody> requestChangeAmbulance(@Body RequestChangeAmbulance requestChangeAmbulance);
    @POST(API.URL+"v1")
    Call<ResponseBody> requestTrackingLatLong(@Body RequestTrackingLatLong requestTrackingLatLong);
    @POST(API.URL+"v1")
    Call<ResponseBody> requestKejadian(@Body RequestKejadian requestKejadian);
    @Multipart
    @POST(API.URL+"v1/tindakan")
    Call<ResponseBody> fileUpload(
          //  @Part("sender_information") RequestBody description,
            @Part("apiKey") RequestBody apiKey,
            @Part("signature") RequestBody signature,
            @Part("id_kejadian") RequestBody id_kejadian,
            @Part("rencana_tindakan") RequestBody rencana_tindakan,
            @Part MultipartBody.Part file);

}


