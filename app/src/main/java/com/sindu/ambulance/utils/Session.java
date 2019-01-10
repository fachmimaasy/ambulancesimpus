package com.sindu.ambulance.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Session {
    private static int attempt = 1;
    private static Context context = null;
    private static SharedPreferences preferences;
    private static Editor editor;

    int public_MODE = 0;
    public static final String IS_NAV = "DisableNavigation";

    public Session(Context context){
        this.context = context;
        preferences = context.getSharedPreferences(IS_NAV, public_MODE);
        editor = preferences.edit();
    }

    public static void save(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public static void save(String key, Integer value) {
        save(key, String.valueOf(value));
    }

    public static void save(String key, Long value) {
        save(key, String.valueOf(value));
    }

    public static String get(String key) {
        return preferences.getString(key, (String)null);
    }

    public static Boolean contains(String key) {
        return Boolean.valueOf(preferences.contains(key));
    }

    public static void removeKey(String key) {
        editor.remove(key);
        editor.commit();
    }

    public static void clear() {
        editor.clear();
        editor.commit();
    }

}