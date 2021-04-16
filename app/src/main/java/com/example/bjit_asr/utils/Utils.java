package com.example.bjit_asr.utils;

import android.content.Context;
import android.view.View;

import com.example.bjit_asr.R;
import com.google.android.material.snackbar.Snackbar;

public class Utils {
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
}
