package com.sindu.ambulance.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by XGibar on 24/10/2017.
 */

public class API {
    public static final String URL = "http://ambulan.kinaryatama.id/api/"; //URL

    //session
    public static String APIKEY = "apikey"; //URL
    public static String POSISITION = "position";
    public static String USER = "user";
    public static String AMBULANCE = "ambulance";
    public static String KEJADIAN = "kejadian";


    //History
    public static JSONObject JSONUSER = null;
    public static JSONObject JSONAMBULANCE= null;


    //DATA USER
    public static Uri file;
    public static String path;


    public static boolean isLoggedIn = false;

    //RETROFIT
    public static ProgressDialog mProgressDialog;
    public static final boolean showLog = false;
    public static final int DEFAULT_RETRIES = 0;


    public static String SHA1(String input) throws NoSuchAlgorithmException {

        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());

        StringBuffer SHAStr = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            SHAStr.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }

        return SHAStr.toString();
    }

    public static String getNumberFormat(int number){
        NumberFormat numberFormatter = NumberFormat.getNumberInstance(Locale.GERMANY);
        String output = numberFormatter.format(number);
        return output;
    }


    public static Interface initRetrofit(boolean showLog) {
        Retrofit retrofit;
        if (showLog == true) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            final OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .addInterceptor(interceptor).build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        } else {
            final OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(Interface.class);
    }

    public static void retryDialog(final Context context, final Call<ResponseBody> call, final Callback<ResponseBody> callback, final int tanda, final boolean dialog) {
        if (dialog == false)
            new AlertDialog.Builder(context)
                    .setTitle("Gagal Terhubung")
                    .setMessage("Pastikan sudah terhubung dengan jaringan yang stabil")
                    .setCancelable(false)
                    .setNegativeButton("Keluar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    ((Activity) context).setResult(tanda);
                                    ((Activity) context).finish();
                                }
                            }
                    )
                    .setPositiveButton("Coba lagi", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            if (dialog == true) {
                                mProgressDialog = new ProgressDialog(context);
                                mProgressDialog.setIndeterminate(true);
                                mProgressDialog.setCancelable(false);
                                mProgressDialog.setMessage("Loading...");
                                mProgressDialog.show();
                                call.clone().enqueue(new RetryableCallback<ResponseBody>(call) {

                                    @Override
                                    public void onFinalResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        mProgressDialog.dismiss();
                                        callback.onResponse(call, response);
                                    }

                                    @Override
                                    public void onFinalFailure(Call<ResponseBody> call, Throwable t) {
                                        mProgressDialog.dismiss();
                                        callback.onFailure(call, t);
                                    }
                                });
                            } else {
                                call.clone().enqueue(new RetryableCallback<ResponseBody>(call) {

                                    @Override
                                    public void onFinalResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        //mProgressDialog.dismiss();
                                        callback.onResponse(call, response);
                                    }

                                    @Override
                                    public void onFinalFailure(Call<ResponseBody> call, Throwable t) {
                                        //mProgressDialog.dismiss();
                                        callback.onFailure(call, t);
                                    }
                                });
                            }

                        }
                    }).create().show();


    }

    //RETROFIT RETRIES//
    public static <T> void enqueueWithRetry(Context context, Call<T> call, final int retryCount, final Callback<T> callback, boolean dialog) {
        if (dialog == true) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.show();
            call.enqueue(new RetryableCallback<T>(call, retryCount) {

                @Override
                public void onFinalResponse(Call<T> call, Response<T> response) {
                    mProgressDialog.dismiss();
                    callback.onResponse(call, response);
                }

                @Override
                public void onFinalFailure(Call<T> call, Throwable t) {
                    mProgressDialog.dismiss();
                    callback.onFailure(call, t);
                }
            });
        } else {
            call.enqueue(new RetryableCallback<T>(call, retryCount) {

                @Override
                public void onFinalResponse(Call<T> call, Response<T> response) {
                    callback.onResponse(call, response);
                }

                @Override
                public void onFinalFailure(Call<T> call, Throwable t) {
                    callback.onFailure(call, t);
                }
            });
        }
    }

    public static <T> void enqueueWithRetry(Context context, Call<T> call, final Callback<T> callback, boolean dialog) {
        enqueueWithRetry(context, call, DEFAULT_RETRIES, callback, dialog);
    }

    public static boolean isCallSuccess(Response response) {
        int code = response.code();
        return (code >= 200 && code < 400);
    }

}

abstract class RetryableCallback<T> implements Callback<T> {

    private int totalRetries = 0;
    private static final String TAG = RetryableCallback.class.getSimpleName();
    private final Call<T> call;
    private int retryCount = 0;

    public RetryableCallback(Call<T> call, int totalRetries) {
        this.call = call;
        this.totalRetries = totalRetries;
    }

    public RetryableCallback(Call<T> call) {
        this.call = call;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (!API.isCallSuccess(response))
            if (retryCount++ < totalRetries) {
                retry();
            } else
                onFinalResponse(call, response);
        else
            onFinalResponse(call, response);
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        Log.e(TAG, t.getMessage());
        if (retryCount++ < totalRetries) {
            retry();
        } else
            onFinalFailure(call, t);
    }

    public void onFinalResponse(Call<T> call, Response<T> response) {

    }

    public void onFinalFailure(Call<T> call, Throwable t) {
    }

    private void retry() {
        call.clone().enqueue(this);
    }

}


