package com.sindu.ambulance.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by palapabeta on 13/12/17.
 */

public class RequestListAmbulance {

    @SerializedName("HeaderCode")
    @Expose
    private String headerCode;
    @SerializedName("apiKey")
    @Expose
    private String apiKey;
    @SerializedName("signature")
    @Expose
    private String signature;

    public RequestListAmbulance(String headerCode, String apiKey, String signature) {
        this.headerCode = headerCode;
        this.apiKey = apiKey;
        this.signature = signature;
    }

}
