package com.sindu.ambulance.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasdassdaterter--- on 12/13/2017.
 */

public class RequestTrackingTwo {

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

    public RequestTrackingTwo(String headerCode, Data data, String apiKey, String signature) {
        this.headerCode = headerCode;
        this.data = data;
        this.apiKey = apiKey;
        this.signature = signature;
    }

    public static class Data{
        @SerializedName("id_kejadian")
        @Expose
        private String id_kejadian;
        @SerializedName("latitude")
        @Expose
        private String latitude;
        @SerializedName("longitude")
        @Expose
        private String longitude;
        public Data(String id_kejadian, String latitude, String longitude){
            this.id_kejadian = id_kejadian;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}