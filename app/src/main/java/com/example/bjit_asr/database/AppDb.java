package com.example.bjit_asr.database;

import android.content.Context;

import androidx.room.Room;

public class AppDb
{
    private static AppDatabase single_instance = null;
    private AppDb(Context context)
    {
        single_instance = Room.databaseBuilder(context,
                AppDatabase.class, "bsbc-asr-ticktalk.db")
                .allowMainThreadQueries()
                .build();
    }

    public static AppDatabase getInstance(Context context) {
        if (single_instance == null)
            new AppDb(context);
        return single_instance;
    }

    public static AppDatabase getInstance()
    {
        return single_instance;
    }
}
