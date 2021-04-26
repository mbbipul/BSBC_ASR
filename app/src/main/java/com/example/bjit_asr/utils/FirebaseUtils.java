package com.example.bjit_asr.utils;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUtils {
    public static final String DATABASE_ROOT_PATH= "ux_bjit_asr";

    public static FirebaseFirestore getFireStoreDb() {
        return FirebaseFirestore.getInstance();
    }

    public static CollectionReference getDbRef() {
        return getFireStoreDb().collection(DATABASE_ROOT_PATH);
    }
}
