package com.example.blooddonationkotli.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.blooddonationkotli.R;
import com.example.blooddonationkotli.Recievers.NetworkReciever;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SplashScreen extends AppCompatActivity implements LocationListener {
    FirebaseUser user;
    FirebaseAuth mAuth;
    LocationManager locationManager;
    double Lat, lang;
    NetworkReciever networkReciever;
    SweetAlertDialog requestAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        requestAccess = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        Dexter.withContext(SplashScreen.this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 5, SplashScreen.this);
                        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                            try {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (FirebaseAuth.getInstance().getCurrentUser() != null && FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                                            startActivity(new Intent(SplashScreen.this, Dashboard.class));
                                        } else {
                                            startActivity(new Intent(SplashScreen.this, MainActivity.class));
                                        }
                                    }
                                }, 4000);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            asktoLocationAccess();
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        requestPermission();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    private void requestPermission() {
        requestAccess.setTitleText("Permission Required")
                .setContentText("Please allow application to use location")
                .setConfirmText("Allow")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                }).show();


//        new android.app.AlertDialog.Builder(this)
//                .setTitle("Permisison Required")
//                .setMessage("Please allow application to use this feature")
//                .setCancelable(false)
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent();
//                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                        Uri uri = Uri.fromParts("package", getPackageName(), null);
//                        intent.setData(uri);
//                        startActivity(intent);
//                    }
//                })
//                .setNegativeButton("CANCLE", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                }).show();
    }

    public void asktoLocationAccess() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Enable GPS")
                .setContentText("Please Enable your device locaiton to continue using app")
                .showCancelButton(true)
                .setConfirmText("Yes,Enable").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                SplashScreen.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).show();
//        new AlertDialog.Builder(SplashScreen.this)
//                .setTitle("Request Locaiton Provide")
//                .setMessage("Please turn on device location ")
//                .setPositiveButton("Locaitons", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
//                        SplashScreen.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                        paramDialogInterface.dismiss();
//                    }
//                })
//                .setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .show();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Lat = location.getLatitude();
        lang = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}