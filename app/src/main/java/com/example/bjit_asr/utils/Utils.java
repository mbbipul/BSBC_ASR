package com.example.bjit_asr.utils;

import android.content.Context;
import android.media.AudioManager;
import android.provider.Settings;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.bjit_asr.R;
import com.google.android.material.snackbar.Snackbar;

public class Utils {
    public static final int REQUEST_RECORD_AUDIO_PERMISSION_CODE = 14334;

    public static void showSnackMessage(View layout,String message){
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


}
