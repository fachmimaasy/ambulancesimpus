package com.sindu.ambulance.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by XGibar on 24/10/2017.
 */

public class RequestLogin {

    @SerializedName("HeaderCode")
    @Expose
    private String headerCode;
    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("signature")
    @Expose
    private String signature;

    public RequestLogin(String headerCode, Data data, String signature) {
        this.headerCode = headerCode;
        this.data = data;
        this.signature = signature;
    }
    public static class Data{
        @SerializedName("email")
        @Expose
        private String username;
        @SerializedName("password")
        @Expose
        private String password;
        public Data(String username, String password){
            this.username = username;
            this.password = password;
        }
    }
}