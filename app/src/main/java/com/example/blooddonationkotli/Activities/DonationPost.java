package com.example.blooddonationkotli.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.blooddonationkotli.Model.DonationModel;
import com.example.blooddonationkotli.R;
import com.example.blooddonationkotli.Recievers.NetworkReciever;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DonationPost extends AppCompatActivity implements LocationListener {
    TextInputLayout edtname, edtPhone, edtAddress, edtlname, edtMessage;
    DatabaseReference dbref;
    FirebaseAuth firebaseAuth;
    String uName, email, phone, address, message, city;
    FusedLocationProviderClient mFuesdLocaitonProvide;
    NetworkReciever networkReciever = new NetworkReciever();
    Dialog progressDialog;
    Spinner spinnerBloodGroup;
    Double lat, lang;
    TextView bloodGroup;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_post);
        initView();
        getLocation();
        spinnerListener();
        getUserIfo();


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void spinnerListener() {
        spinnerBloodGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bloodGroup.setText(spinnerBloodGroup.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 5, DonationPost.this);
    }

    private void getUserIfo() {
        dbref.child("Users").child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    uName = snapshot.child("name").getValue(String.class);
                    phone = snapshot.child("phone").getValue(String.class);
                    edtname.getEditText().setText(uName);
                    edtPhone.getEditText().setText(phone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initView() {
        edtname = findViewById(R.id.edtdonateFname);
        edtPhone = findViewById(R.id.edtDonatePhone);
        edtAddress = findViewById(R.id.edtDonateAddress);
        edtMessage = findViewById(R.id.edtDonorMessage);
        dbref = FirebaseDatabase.getInstance().getReference();
        spinnerBloodGroup = findViewById(R.id.donateBloodGroup);
        bloodGroup = findViewById(R.id.tvdonateBloodField);
        firebaseAuth = FirebaseAuth.getInstance();
        toolbar = findViewById(R.id.toolbardonaiton);
        progressDialog = new Dialog(this);
        View mView = getLayoutInflater().inflate(R.layout.progress_dialog_wait, null);
        progressDialog.setContentView(mView);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.show();
    }

    public void PostDonation(View view) {

        final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setTitleText("Please wait").show();
        final DatabaseReference refrence = FirebaseDatabase.getInstance().getReference();
        final DonationModel donationModel = new DonationModel();
        String key = refrence.child("Donations").push().getKey();

        donationModel.setName(uName);
        donationModel.setAddress(address);
        donationModel.setLat(String.valueOf(lat));
        donationModel.setLang(String.valueOf(lang));
        donationModel.setDescriptions(edtMessage.getEditText().getText().toString());
        donationModel.setBloodGoup(bloodGroup.getText().toString());
        donationModel.setUserKey(firebaseAuth.getCurrentUser().getUid());
        donationModel.setPhone(edtPhone.getEditText().getText().toString());
        donationModel.setCity(city);
        donationModel.setPushKey(key);
        refrence.child("Donations").child(key).setValue(donationModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    refrence.child("My Donations").child(firebaseAuth.getCurrentUser().getUid()).push().setValue(donationModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                sweetAlertDialog.dismissWithAnimation();
                                Snackbar.make(findViewById(R.id.linearLayout5), "Thank you for your nobel deed", Snackbar.LENGTH_SHORT).show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(DonationPost.this, Dashboard.class));
                                    }
                                }, 1000);
                            } else {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(networkReciever, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkReciever);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lang = location.getLongitude();
            progressDialog.dismiss();
            try {
                Geocoder geocoder = new Geocoder(DonationPost.this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                String _addres = addresses.get(0).getAddressLine(0);
                String cityName = addresses.get(0).getLocality();
                address = _addres;
                edtAddress.getEditText().setText(address);
                city = cityName;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            progressDialog.dismiss();
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
}