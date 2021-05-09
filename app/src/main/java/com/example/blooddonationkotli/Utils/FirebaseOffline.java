package com.example.blooddonationkotli.Utils;

import com.google.firebase.database.FirebaseDatabase;

public class FirebaseOffline {
    static FirebaseDatabase mDatabase;
    public static FirebaseDatabase getSync() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }
}
