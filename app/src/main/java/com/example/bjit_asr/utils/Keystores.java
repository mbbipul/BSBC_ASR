package com.example.bjit_asr.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class Keystores {
    private static Keystores store;
    private SharedPreferences SP;
    private static String filename="conversation_id";

    private Keystores(Context context) {
        SP = context.getApplicationContext().getSharedPreferences(filename,0);
    }

    public static Keystores getInstance(Context context) {
        if (store == null) {
            Log.v("Keystore","NEW STORE");
            store = new Keystores(context);
        }
        return store;
    }

    public void put(String key, String value) {
        Editor editor = SP.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String get(String key) {
        return SP.getString(key, null);
    }

    public int getInt(String key) {
        return SP.getInt(key, 0);
    }

    public void putInt(String key, int num) {
        Editor editor = SP.edit();
        editor.putInt(key, num);
        editor.commit();
    }


    public void clear(){
        Editor editor = SP.edit();
        editor.clear();
        editor.commit();
    }

    public void remove(){
        Editor editor = SP.edit();
        editor.remove(filename);
        editor.commit();
    }
}
