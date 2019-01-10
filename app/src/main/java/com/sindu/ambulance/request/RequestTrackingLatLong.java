package com.sindu.ambulance.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by palapabeta on 18/12/17.
 */

public class RequestTrackingLatLong {

    @SerializedName("HeaderCode")
    @Expose
    private String headerCode;
    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("apiKey")
    @Expose
    private String apiKey;
    @SerializedName("signature")
    @Expose
    private String signature;

    public RequestTrackingLatLong(String headerCode, Data data, String apiKey, String signature) {
        this.headerCode = headerCode;
        this.data = data;
        this.apiKey = apiKey;
        this.signature = signature;
    }

    public static class Data{
        @SerializedName("latitude")
        @Expose
        private String latitude;
        @SerializedName("longitude")
        @Expose
        private String longitude;
        public Data(String latitude, String longitude){
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}