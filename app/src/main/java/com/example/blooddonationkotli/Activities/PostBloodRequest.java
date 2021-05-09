package com.example.blooddonationkotli.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonationkotli.Model.BloodRequestModel;
import com.example.blooddonationkotli.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PostBloodRequest extends AppCompatActivity implements View.OnClickListener, LocationListener {
    private static final int REFRESH_TIME = 500;
    private static final int DISTANCE = 5;
    LinearLayout postBloodrequest, postdialog, pickDate, pickLocation;
    boolean isUp;
    int mYear, mMonth, mDay;
    TextInputLayout dateInput, edtAddress, fName, lName, mPhone, description, edtBloodfor;
    Spinner bloodGroupSpinner;
    TextView bloodGroup;
    LocationManager locationManager;
    ProgressDialog progressDialog;
    String bloodFor, mycity;
    Double lat, lang;
    DatabaseReference postRequest, requestRefrense;
    Dialog dialog;
    RecyclerView recyclerView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_blood_request);
        initView();
        getCurrentLocation();
        selectBloodGroup();
        fetchRequests();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        postdialog.setVisibility(View.INVISIBLE);
        isUp = false;
        postBloodrequest.setOnClickListener(this);
        pickDate.setOnClickListener(this);
        pickLocation.setOnClickListener(this);
    }

    private void fetchRequests() {
        requestRefrense = FirebaseDatabase.getInstance().getReference();
        requestRefrense = requestRefrense.child("Blood Requests");
        FirebaseRecyclerOptions<BloodRequestModel> options =
                new FirebaseRecyclerOptions.Builder<BloodRequestModel>().setQuery(requestRefrense, BloodRequestModel.class).build();
        FirebaseRecyclerAdapter<BloodRequestModel, BloodRequests.BloodRequestViewHolder> adapter = new FirebaseRecyclerAdapter<BloodRequestModel, BloodRequests.BloodRequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull BloodRequests.BloodRequestViewHolder holder, int position, @NonNull BloodRequestModel model) {
                holder.muserName.setText(model.getName());
                holder.mBloodGourp.setText(model.getBloodGroup());
                holder.mPhone.setText(model.getPhone());
                holder.mAddress.setText(model.getAddress());
                holder.mdesc.setText(model.getDescription());
                holder.tvDate.setText(model.getDate());
                holder.btnDonate.setVisibility(View.GONE);
                holder.btnDonate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View mView = getLayoutInflater().inflate(R.layout.donaiton_confirmation_dialog, null);
                        AlertDialog.Builder builder = new AlertDialog.Builder(PostBloodRequest.this);
                        builder.setView(mView)
                                .setCancelable(false)
                                .setNegativeButton("No, I'll Donate Latter", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                        Button btnConfirm = mView.findViewById(R.id.btnConfirm);
                    }
                });
            }

            @NonNull
            @Override
            public BloodRequests.BloodRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View mView = LayoutInflater.from(PostBloodRequest.this).inflate(R.layout.custome_bloodrequest_layout, parent, false);
                return new BloodRequests.BloodRequestViewHolder(mView);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    public void postRequest(View view) {
        if (isValid() == true) {
            dialog.show();
            String mName = fName.getEditText().getText().toString() + " " + lName.getEditText().getText().toString();
            String date = dateInput.getEditText().getText().toString();
            String address = edtAddress.getEditText().getText().toString();
            String phone = mPhone.getEditText().getText().toString();
            String desc = description.getEditText().getText().toString();
            String group = bloodGroup.getText().toString();
            postRequest = FirebaseDatabase.getInstance().getReference();
            postRequest = postRequest.child("Blood Requests");
            DatabaseReference myRequests = FirebaseDatabase.getInstance().getReference();
            final String requestId = postRequest.push().getKey();
            final BloodRequestModel requestModel = new BloodRequestModel();
            requestModel.setName(mName);
            requestModel.setAddress(address);
            requestModel.setBlood_for(bloodFor);
            requestModel.setDate(date);
            requestModel.setPhone(phone);
            requestModel.setDescription(desc);
            requestModel.setBloodGroup(group);
            requestModel.setRequestId(requestId);
            requestModel.setLat(String.valueOf(lat));
            requestModel.setLang(String.valueOf(lang));
            requestModel.setuId(FirebaseAuth.getInstance().getCurrentUser().getUid());
            requestModel.setCity(mycity);
            myRequests.child("My Requests").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(requestId).setValue(requestModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                        postRequest.child(requestId).setValue(requestModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    dialog.dismiss();
                                    Snackbar.make(findViewById(R.id.postDialog), "Success! Blood Request Have Been posted", Snackbar.LENGTH_LONG).show();
                                    slideDown(postdialog);
                                } else {
                                    dialog.dismiss();
                                    Snackbar.make(findViewById(R.id.postDialog), "Sorry! Blood Request Not Posted", Snackbar.LENGTH_LONG).show();
                                }
                            }
                        });
                }
            });
        } else {
            Snackbar.make(findViewById(R.id.postDialog), "Error! Please Fill The Required Fileds", Snackbar.LENGTH_LONG).show();
        }
    }

    private void initView() {
        postBloodrequest = findViewById(R.id.postbloodRequest);
        postdialog = findViewById(R.id.postDialog);
        pickDate = findViewById(R.id.llpickDate);
        dateInput = findViewById(R.id.edttypeOfrequire);
        bloodGroupSpinner = findViewById(R.id.bloodGroups);
        bloodGroup = findViewById(R.id.bloodGroupField);
        pickLocation = findViewById(R.id.pickLocation);
        edtAddress = findViewById(R.id.edtPickAddress);
        fName = findViewById(R.id.edtbloodforFname);
        lName = findViewById(R.id.edtbloodforLname);
        mPhone = findViewById(R.id.edtPoostBloodPhone);
        description = findViewById(R.id.postrequestDesc);
        toolbar = findViewById(R.id.toolbarpostRequest);
        dialog = new Dialog(this);
        View mView = getLayoutInflater().inflate(R.layout.progress_dialog_wait, null);
        dialog.setContentView(mView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        edtBloodfor = findViewById(R.id.edtBloodFor);
        recyclerView = findViewById(R.id.recyclerViewPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        if (v == postBloodrequest) {
            if (isUp) {
                slideDown(postdialog);
            } else {
                slideUp(postdialog);
            }
            isUp = !isUp;
        }
        if (v == pickDate) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    dateInput.getEditText().setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                }
            }, mYear, mMonth, mDay);
            datePickerDialog.setTitle("Pick Date");
            datePickerDialog.show();
        }
        if (v == pickLocation) {
            getCurrentLocation();
        }
    }

    public void slideUp(View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        view.startAnimation(animate);
    }


    // slide the view from its current position to below itself
    public void slideDown(View view) {
        view.setVisibility(View.INVISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        view.startAnimation(animate);
    }

    private void selectBloodGroup() {
        bloodGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bloodGroup.setText(bloodGroupSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        try {
            progressDialog = new ProgressDialog(PostBloodRequest.this);
            progressDialog.setTitle("Fetching Locaiton Please Wait");
            progressDialog.show();
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, PostBloodRequest.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Toast.makeText(PostBloodRequest.this, location.getLatitude() + "," + location.getLongitude(), Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();

        try {
            Geocoder geocoder = new Geocoder(PostBloodRequest.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            lat = location.getLatitude();
            lang = location.getLongitude();
            String _addres = addresses.get(0).getAddressLine(0);
            mycity = addresses.get(0).getLocality();
            edtAddress.getEditText().setText(_addres);
        } catch (IOException e) {
            e.printStackTrace();
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

    public void forMyself(View view) {
        CheckBox checkBox = findViewById(R.id.selfCheckBox);
        if (checkBox.isChecked()) {
            if (bloodFor == null) {
                bloodFor = "Self";
                edtBloodfor.getEditText().setText(bloodFor);
            } else {
                bloodFor = "";
                bloodFor = "Self";
                edtBloodfor.getEditText().setText(bloodFor);
            }
        }
    }

    public void forOthers(View view) {
        CheckBox checkBox = findViewById(R.id.otherCheckbox);
        if (checkBox.isChecked()) {
            if (bloodFor == null) {
                bloodFor = "Others";
                edtBloodfor.getEditText().setText(bloodFor);
            } else {
                bloodFor = "";
                bloodFor = "Others";
                edtBloodfor.getEditText().setText(bloodFor);
            }
        }
    }

    public boolean isValid() {
        if (fName.getEditText().getText().toString().isEmpty()) {
            fName.setError("Name Required");
            return false;
        } else if ((lName.getEditText().getText().toString().isEmpty())) {
            lName.setError("Name Required");
            return false;
        } else if (edtBloodfor.getEditText().getText().toString().isEmpty()) {
            edtBloodfor.setError("Field Required");
            return false;
        } else if (dateInput.getEditText().getText().toString().isEmpty()) {
            dateInput.setError("Date Required");
            return false;
        } else if (edtAddress.getEditText().getText().toString().isEmpty()) {
            edtAddress.setError("Address Required");
            return false;
        } else if (description.getEditText().getText().toString().isEmpty()) {
            description.setError("Description Required");
            return false;
        } else {
            return true;
        }

    }
}
