package com.example.blooddonationkotli.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blooddonationkotli.Model.BloodRequestModel;
import com.example.blooddonationkotli.Model.NotificaiotnsModel;
import com.example.blooddonationkotli.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class BloodRequests extends AppCompatActivity implements View.OnClickListener, LocationListener {
    DatabaseReference bloodRequests, userRefrence, notificaitonRefrence;
    RecyclerView recyclerView;
    Toolbar toolbar;
    String city, userName, imageUrl;
    FusedLocationProviderClient fusedLocationProviderClient;
    LinearLayout postBloodrequest, postdialog, pickDate, pickLocation;
    boolean isUp;
    int mYear, mMonth, mDay;
    TextInputLayout dateInput, edtAddress, fName, lName, mPhone, description, edtBloodfor;
    Spinner bloodGroupSpinner;
    TextView bloodGroup;
    LocationManager locationManager;
    String bloodFor, mycity;
    Double lat, lang;
    String Lat, Lang;
    DatabaseReference postRequest;
    Dialog dialog;
    FloatingActionButton togglePost;
    SweetAlertDialog sweetAlertDialog;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_requests);
        initViews();
        selectBloodGroup();
        getCurrentUserDetails();
        //getLocaiton();
        // getcityName();
        getCurrentLocation();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        postdialog.setVisibility(View.INVISIBLE);
        isUp = false;
        togglePost.setOnClickListener(this);
        pickDate.setOnClickListener(this);
        pickLocation.setOnClickListener(this);
        imageView.setOnClickListener(this);
    }

    private void getCurrentUserDetails() {
        userRefrence = FirebaseDatabase.getInstance().getReference("Users");
        userRefrence.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    userName = snapshot.child("name").getValue(String.class);
                    imageUrl = snapshot.child("imageUrl").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }


//    @SuppressLint("MissingPermission")
//    private void getcityName() {
//        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 5, BloodRequests.this);
//    }


    private void getRequests(String cityName) {
        Query query = bloodRequests.child("Blood Requests").orderByChild("city").equalTo(cityName);
        FirebaseRecyclerOptions<BloodRequestModel> options =
                new FirebaseRecyclerOptions.Builder<BloodRequestModel>().setQuery(query, BloodRequestModel.class).build();
        FirebaseRecyclerAdapter<BloodRequestModel, BloodRequestViewHolder> adapter = new FirebaseRecyclerAdapter<BloodRequestModel, BloodRequestViewHolder>(options) {
            @SuppressLint("StaticFieldLeak")
            @Override
            protected void onBindViewHolder(@NonNull final BloodRequestViewHolder holder, int position, @NonNull final BloodRequestModel model) {
                holder.muserName.setText(model.getName());
                holder.mBloodGourp.setText(model.getBloodGroup());
                holder.mAddress.setText(model.getAddress());
                holder.tvDate.setText(model.getDate());
                holder.mAddress.setText(model.getAddress());
                holder.tvBloodReqDesc.setText(model.getDescription());

                Lat = model.getLat();
                Lang = model.getLang();

                String imageUrl = "https://maps.googleapis.com/maps/api/staticmap?center=" + lat + "," + lang + "=&zoom=17&size=600x300&maptype=normal\n" +
                        "&markers=color:red%7Clabel:C%7C" + lat + "," + lang + "\n" +
                        "&key=";
                Picasso.get().load(imageUrl).into(holder.imgNeedPlace);
                final String uId = model.getuId();
                if (uId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    holder.btnDonate.setVisibility(View.GONE);
                }

                holder.btnDonate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final SweetAlertDialog dialog = new SweetAlertDialog(BloodRequests.this, SweetAlertDialog.BUTTON_CONFIRM);
                        dialog.setTitleText("Confirmation");
                        dialog.setContentText("Are you sure. This will be so noice of you");
                        dialog.setConfirmText("Confirm");
                        dialog.show();
                        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                NotificaiotnsModel notificaiotnsModel = new NotificaiotnsModel();
                                notificaiotnsModel.setFrom(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                notificaiotnsModel.setType("respond");
                                notificaiotnsModel.setSeen("false");
                                DatabaseReference notiRefrence = FirebaseDatabase.getInstance().getReference("notifications");
                                notiRefrence.child(uId).push().setValue(notificaiotnsModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            bloodRequests.child("Blood Requests").child(model.getRequestId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        new AlertDialog.Builder(BloodRequests.this)
                                                                .setTitle("Thank you")
                                                                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog1, int which) {
                                                                        dialog1.dismiss();
                                                                        dialog.dismissWithAnimation();
                                                                    }
                                                                }).setMessage("Thank you for your Nobel deed")
                                                                .show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        });

                    }
                });

                holder.muserName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(BloodRequests.this, UserProfile.class);
                        intent.putExtra("userId", model.getuId());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public BloodRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View mView = LayoutInflater.from(BloodRequests.this).inflate(R.layout.cutome_posts_layouts, parent, false);
                return new BloodRequestViewHolder(mView);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    private void initViews() {
        togglePost = findViewById(R.id.btnTooglepostshit);
        bloodRequests = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.recyclerViewbloodRequest);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        toolbar = findViewById(R.id.toolbarRequests);
        sweetAlertDialog = new SweetAlertDialog(BloodRequests.this, SweetAlertDialog.PROGRESS_TYPE);
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
        dialog = new Dialog(this);
        View view = getLayoutInflater().inflate(R.layout.progress_dialog_wait, null);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        edtBloodfor = findViewById(R.id.edtBloodFor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        imageView = findViewById(R.id.closePostSheet);
        sweetAlertDialog.setTitleText("Please Wait");
        sweetAlertDialog.setContentText("Fetching Locations");
        sweetAlertDialog.show();
    }


    @SuppressLint("MissingPermission")
    public void getLocaiton() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    try {
                        Geocoder geocoder = new Geocoder(BloodRequests.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        lat = location.getLatitude();
                        lang = location.getLongitude();
                        String _addres = addresses.get(0).getAddressLine(0);
                        mycity = addresses.get(0).getLocality();
                        edtAddress.getEditText().setText(_addres);
                        getRequests(mycity);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, BloodRequests.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Otherway to get Locaiton

    static class BloodRequestViewHolder extends RecyclerView.ViewHolder {
        TextView mBloodGourp, muserName, mPhone, mAddress, mdesc, tvDate, tvBloodReqDesc;
        TextView btnDonate;
        ImageView imgNeedPlace;

        public BloodRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            muserName = itemView.findViewById(R.id.tvBloodRequestName);
            tvDate = itemView.findViewById(R.id.tvBloodPostDate);
            mBloodGourp = itemView.findViewById(R.id.tvbloodPostBloodGroup);
            mAddress = itemView.findViewById(R.id.tvAddresBloodPost);
            imgNeedPlace = itemView.findViewById(R.id.needAtPlaceImage);
            btnDonate = itemView.findViewById(R.id.donateBloodBtn);
            tvBloodReqDesc = itemView.findViewById(R.id.tvbloodPostDecription);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        if (v == togglePost) {
            if (isUp) {
                slideDown(postdialog);
            } else {
                slideUp(postdialog);
                Animation slide = AnimationUtils.loadAnimation(this, R.anim.slide_in);
                imageView.startAnimation(slide);
                imageView.setVisibility(View.VISIBLE);
            }
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
        if (v == imageView) {
            slideDown(postdialog);
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
            imageView.startAnimation(animation);
            imageView.setVisibility(View.GONE);
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
                                    Animation slide = AnimationUtils.loadAnimation(BloodRequests.this, R.anim.slide_in);
                                    imageView.startAnimation(slide);
                                    imageView.setVisibility(View.VISIBLE);
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

    @Override
    public void onLocationChanged(@NonNull Location location) {
        sweetAlertDialog.dismissWithAnimation();
        try {
            Geocoder geocoder = new Geocoder(BloodRequests.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            lat = location.getLatitude();
            lang = location.getLongitude();
            String _addres = addresses.get(0).getAddressLine(0);
            mycity = addresses.get(0).getLocality();
            edtAddress.getEditText().setText(_addres);
            getRequests(mycity);
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

    private class getMap extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... voids) {
            Bitmap bitmap = null;

            return null;
        }
    }

}