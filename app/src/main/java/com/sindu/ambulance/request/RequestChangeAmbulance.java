package com.sindu.ambulance.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by dasdassdaterter--- on 12/17/2017.
 */

public class RequestChangeAmbulance {

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

    public RequestChangeAmbulance(String headerCode,Data data, String apiKey, String signature) {
        this.headerCode = headerCode;
        this.apiKey = apiKey;
        this.signature = signature;
        this.data = data;
    }

    public static class Data{
        @SerializedName("id_ambulan")
        @Expose
        private String id_ambulan;

        public Data(String id_ambulan){
            this.id_ambulan = id_ambulan;

        }
    }

}
