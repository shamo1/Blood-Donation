package com.example.blooddonationkotli.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.blooddonationkotli.Model.UserModel;
import com.example.blooddonationkotli.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Setup_Profile extends AppCompatActivity implements LocationListener {
    LocationManager locationManager;
    Dialog progressDialog;
    TextInputLayout edtcity, edtPhone;
    TextView userName, userEmail;
    String gander;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup__profile);
        initViews();
        GoogleSignInAccount gso = GoogleSignIn.getLastSignedInAccount(this);
        userName.setText(gso.getDisplayName());
        userEmail.setText(gso.getEmail());
        progressDialog = new Dialog(this);
        View mViw = getLayoutInflater().inflate(R.layout.progress_dialog_wait, null);
        progressDialog.setContentView(mViw);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void GetCity(View view) {
        if (ContextCompat.checkSelfPermission(Setup_Profile.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Setup_Profile.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }
        if (ContextCompat.checkSelfPermission(Setup_Profile.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(Setup_Profile.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocations();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocations() {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            progressDialog.show();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 5, Setup_Profile.this);
        } else {
            new AlertDialog.Builder(Setup_Profile.this)
                    .setTitle("Request Locaiton Provide")
                    .setMessage("Please turn on device location ")
                    .setPositiveButton("Locaitons", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            Setup_Profile.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            paramDialogInterface.dismiss();
                            getCurrentLocations();
                        }
                    })
                    .setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && (grantResults.length > 0) && grantResults[0] + grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocations();
        } else {
            Snackbar.make(findViewById(R.id.mainlayout), "Permission Denied", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (location != null) {
            progressDialog.dismiss();
            try {
                Geocoder geocoder = new Geocoder(Setup_Profile.this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                String _addres = addresses.get(0).getLocality();
                edtcity.getEditText().setText(_addres);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
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

    public void getMale(View view) {
        if (gander == null) {
            gander = "Male";
        } else {
            gander = "";
            gander = "Male";
        }
    }

    public void getFemale(View view) {
        if (gander == null) {
            gander = "Female";
        } else {
            gander = "";
            gander = "Female";
        }
    }

    private void initViews() {
        userEmail = findViewById(R.id.tvEmailAddress);
        userName = findViewById(R.id.tvUserName);
        edtcity = findViewById(R.id.edtsetCity);
        edtPhone = findViewById(R.id.edtSetPhone);
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
    }

    public void termAndConditions(View view) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Setup_Profile.this);
        View alertView = getLayoutInflater().inflate(R.layout.term_condition, null);
        alertDialog.setPositiveButton("Yes I Agree.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CheckBox checkBox = findViewById(R.id.checkBoxTermand_condition);
                checkBox.setChecked(true);
                dialog.dismiss();
            }
        });
        alertDialog.setView(alertView);
        alertDialog.show();
    }

    public boolean isValid() {
        if (edtcity.getEditText().getText().toString().isEmpty()) {
            edtcity.setError("City Name required");
            return false;
        } else if (edtPhone.getEditText().getText().toString().isEmpty()) {
            edtPhone.setError("Phone Number required");
            return false;
        } else {
            return true;
        }
    }

    public void RegisterProfile(View view) {
        if (isValid() == true) {
            GoogleSignInAccount gso = GoogleSignIn.getLastSignedInAccount(this);
            String city = edtcity.getEditText().getText().toString();
            String phone = edtPhone.getEditText().getText().toString();
            UserModel userModel = new UserModel();
            userModel.setGander(gander);
            userModel.setCity(city);
            userModel.setPhone(phone);
            userModel.setEmail(gso.getEmail());
            userModel.setName(gso.getDisplayName());
            progressDialog.show();
            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
            dbref.child("Users").child(gso.getId()).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        startActivity(new Intent(Setup_Profile.this, Dashboard.class));
                        finish();
                    } else {
                        Snackbar.make(findViewById(R.id.mainlayout), "Error! Profile Update failed", Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Snackbar.make(findViewById(R.id.mainlayout), "Error! Please Fill the Required Fields", Snackbar.LENGTH_SHORT).show();
        }
    }
}