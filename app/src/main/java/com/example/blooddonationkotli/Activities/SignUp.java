package com.example.blooddonationkotli.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.blooddonationkotli.Model.UserModel;
import com.example.blooddonationkotli.R;
import com.example.blooddonationkotli.Recievers.NetworkReciever;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SignUp extends AppCompatActivity implements LocationListener {
    private static final String TAG = "Blood Group";
    TextInputLayout edtfName, edtLname, edtEmail, edtPassword, edtCPassword, edtphone, edtcity;
    String name, email, phone, password, cPass, city, tokenId;
    CheckBox male, female;
    String gander;
    DatabaseReference userRefrence, cityRefrence;
    FirebaseAuth firebaseAuth;
    LocationManager locationManager;
    boolean iseCheck = true;
    Dialog progressDialog;
    NetworkReciever networkReciever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        networkReciever = new NetworkReciever();
        initViews();
        checkBoxValidaiton();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void GetCity(View view) {
        getCurrentLocations();
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocations() {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            progressDialog.show();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 5, SignUp.this);
        } else {
            new AlertDialog.Builder(SignUp.this)
                    .setTitle("Request Locaiton Provide")
                    .setMessage("Please turn on device location ")
                    .setPositiveButton("Locaitons", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            SignUp.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
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


    private void initViews() {
        edtfName = findViewById(R.id.edtregFname);
        edtLname = findViewById(R.id.edtregLname);
        edtEmail = findViewById(R.id.edtregEmail);
        edtPassword = findViewById(R.id.edtregPass);
        edtCPassword = findViewById(R.id.edtregCpass);
        edtphone = findViewById(R.id.edtregPhone);
        edtcity = findViewById(R.id.edtPickCity);
        male = findViewById(R.id.maleCheckbbox);
        female = findViewById(R.id.femaleCheckbox);
        firebaseAuth = FirebaseAuth.getInstance();
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        userRefrence = FirebaseDatabase.getInstance().getReference();
        cityRefrence = FirebaseDatabase.getInstance().getReference();
        progressDialog = new Dialog(this);
        View mViw = getLayoutInflater().inflate(R.layout.progress_dialog_wait, null);
        progressDialog.setContentView(mViw);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignUp.this);
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

    public void register(View view) {
        if (isValid() == true) {
            progressDialog.show();
            tokenId = FirebaseInstanceId.getInstance().getToken();
            name = edtfName.getEditText().getText().toString() + " " + edtLname.getEditText().getText().toString();
            email = edtEmail.getEditText().getText().toString();
            phone = edtphone.getEditText().getText().toString();
            password = edtPassword.getEditText().getText().toString();
            cPass = edtCPassword.getEditText().getText().toString();
            city = edtcity.getEditText().getText().toString();
            if (password.equals(cPass)) {
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Snackbar.make(findViewById(R.id.parentLayout), "Verification email has sent", Snackbar.LENGTH_LONG).show();
                                        UserModel userModel = new UserModel();
                                        userModel.setEmail(email);
                                        userModel.setName(name);
                                        userModel.setCity(city);
                                        userModel.setPhone(phone);
                                        userModel.setGander(gander);
                                        userModel.setTokenId(tokenId);
                                        userModel.setImageUrl("null");
                                        userModel.setuId(firebaseAuth.getCurrentUser().getUid());
                                        userRefrence.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    DatabaseReference nodeRef = FirebaseDatabase.getInstance().getReference();
                                                    Query query = nodeRef.child("Cities").orderByChild("city").equalTo(city);
                                                    query.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.exists()) {
                                                                progressDialog.dismiss();
                                                                Snackbar.make(findViewById(R.id.parentLayout), "Successfully Register", Snackbar.LENGTH_SHORT).show();
                                                                startActivity(new Intent(SignUp.this, MainActivity.class));
                                                                finish();
                                                            } else {
                                                                HashMap<String, Object> cityMap = new HashMap<>();
                                                                cityMap.put("city", city);
                                                                cityRefrence.child("Cities").push().setValue(cityMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            progressDialog.dismiss();
                                                                            Snackbar.make(findViewById(R.id.parentLayout), "Successfully Register", Snackbar.LENGTH_SHORT).show();
                                                                            startActivity(new Intent(SignUp.this, MainActivity.class));
                                                                            finish();
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });

                                                } else {
                                                    progressDialog.dismiss();
                                                    Snackbar.make(findViewById(R.id.parentLayout), "Sorry! Registration Failed ", Snackbar.LENGTH_SHORT).show();
                                                }
                                            }

                                        });

                                    }
                                }
                            });
                        }
                    }
                });
            } else {
                edtPassword.setError("Password and confirm passwordmust be same");
            }
        } else {
            Snackbar.make(findViewById(R.id.parentLayout), "Error! Fill Required Fields", Snackbar.LENGTH_SHORT).show();
        }
    }

    public void termAndConditions(View view) {
        showAlertDialog();
    }

    private void checkBoxValidaiton() {
        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (male.isChecked()) {
                    if (iseCheck) {
                        gander = "Male";
                        female.setEnabled(false);
                    } else {
                        female.setEnabled(true);
                    }
                    iseCheck = !iseCheck;
                }
                Toast.makeText(SignUp.this, "Male", Toast.LENGTH_SHORT).show();
            }
        });
        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iseCheck) {
                    gander = "Female";
                    male.setEnabled(false);
                } else {
                    male.setEnabled(true);
                }
                iseCheck = !iseCheck;
                Toast.makeText(SignUp.this, "Female", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean isValid() {
        if (edtfName.getEditText().getText().toString().isEmpty()) {
            edtfName.setError("First Name required");
            return false;
        } else if (edtLname.getEditText().getText().toString().isEmpty()) {
            edtLname.setError("Last Name required");
            return false;
        } else if (edtEmail.getEditText().getText().toString().isEmpty()) {
            edtEmail.setError("Email required");
            return false;
        } else if (edtphone.getEditText().getText().toString().isEmpty()) {
            edtphone.setError("Phone number required");
            return false;
        } else if (edtPassword.getEditText().getText().toString().isEmpty()) {
            edtPassword.setError("Password Required required");
            return false;
        } else if (edtcity.getEditText().getText().toString().isEmpty()) {
            edtcity.setError("City name required");
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (location != null) {
            progressDialog.dismiss();
            try {
                Geocoder geocoder = new Geocoder(SignUp.this, Locale.getDefault());
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

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(networkReciever, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkReciever);
    }
}