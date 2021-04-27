package com.example.bjit_asr.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtils {
    public static final String DATABASE_ROOT_PATH= "ux_bjit_asr";

    public static FirebaseDatabase getFirebaseDB() {
        return FirebaseDatabase.getInstance();
    }

    public static DatabaseReference getDbRef() {
        return getFirebaseDB().getReference().child(DATABASE_ROOT_PATH);
    }
}
