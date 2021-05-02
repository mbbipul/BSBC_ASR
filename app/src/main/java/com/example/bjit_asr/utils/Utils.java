package com.example.bjit_asr.utils;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.provider.Settings;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.bjit_asr.R;
import com.google.android.material.snackbar.Snackbar;


public class Utils {
    public static final int REQUEST_RECORD_AUDIO_PERMISSION_CODE = 14334;
    public static final int VIEW_TYPE_MESSAGE_SENT = 1;
    public static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final String USER_ID_KEY = "Remote_User_id";
    public static String RECOGNIZER_LANGUAGE_KEY = "REC_LANG";

    public static void showSnackMessage(Context context,String message){
        View layout = ((Activity) context).getWindow()
                .getDecorView().findViewById(android.R.id.content);
        Snackbar.make(layout, message, Snackbar.LENGTH_LONG)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(layout.getContext().getColor(R.color.red))
                .show();
    }

    public static int muteDevice(AudioManager audioManager){
        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        return audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
    }

    public static void unMuteDevice(AudioManager audioManager,int volume){
        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, volume,
                AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }

    public static int[] getRecognitionProgressViewColor(Context context) {
        int[] colors = {
                ContextCompat.getColor(context, R.color.yellow),
                ContextCompat.getColor(context, R.color.blue),
                ContextCompat.getColor(context, R.color.purple_200),
                ContextCompat.getColor(context, R.color.black),
                ContextCompat.getColor(context, R.color.red)
        };

        return colors;
    }

    public static String getDeviceUniqueId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static String getUserId(Context context){
        Keystores keyStore = Keystores.getInstance(context);
        String userId = keyStore.get(USER_ID_KEY);
        if (userId == null)
            keyStore.put(USER_ID_KEY,getDeviceUniqueId(context));
        return keyStore.get(USER_ID_KEY);
    }

    public static String generateRemoteConversationRoomId(Context context){
        return getUserId(context)+String.valueOf(System.currentTimeMillis()).substring(9);
    }

}
