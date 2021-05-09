package com.example.blooddonationkotli.Recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;

import com.example.blooddonationkotli.Activities.NoConneciton;
import com.example.blooddonationkotli.Utils.NoGPS;

public class NetworkReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            boolean noConneciton = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            if (noConneciton) {
                Intent intent1 = new Intent(context, NoConneciton.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
            }
        }
        if (intent.getAction().matches("android.location.GPS_ENABLED_CHANGE")) {
            boolean isGPS = intent.getBooleanExtra("enabled", false);
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (isGPS) {
                Intent intent2 = new Intent(context, NoGPS.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent2);
            }
        }
    }
}

