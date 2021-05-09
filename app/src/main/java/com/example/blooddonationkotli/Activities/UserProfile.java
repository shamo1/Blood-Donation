package com.example.blooddonationkotli.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.blooddonationkotli.Model.MyfavModel;
import com.example.blooddonationkotli.R;
import com.example.blooddonationkotli.Utils.FirebaseOffline;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {
    Toolbar toolbar;
    CircleImageView imageView;
    TextView tvName, tvEmail, tvPhone, tvAddress, tGander, tvBloodGroup, tvUname, myRequestCount, tvmyDonationcount;
    DatabaseReference dbref;
    FirebaseAuth firebaseAuth;
    Dialog progresDialog;
    ImageView imagAddtoFav;
    String uId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        FirebaseOffline.getSync();
        initViews();
        getDetails();
        getReqeust();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void getReqeust() {
        DatabaseReference reqeustRefrenc = FirebaseDatabase.getInstance().getReference();
        reqeustRefrenc.child("My Requests").child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    long count = snapshot.getChildrenCount();
                    myRequestCount.setText(String.valueOf(count));
                } else {
                    myRequestCount.setText("No Request");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reqeustRefrenc.child("My Donations").child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    long count = snapshot.getChildrenCount();
                    tvmyDonationcount.setText(String.valueOf(count));
                } else {
                    tvmyDonationcount.setText("No Donation");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getDetails() {
        String userId = getIntent().getStringExtra("userId");
        dbref.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    progresDialog.dismiss();
                    String name = snapshot.child("name").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String gander = snapshot.child("gander").getValue(String.class);
//                    String bloodGroup = snapshot.child("name").getValue(String.class);
                    String address = snapshot.child("city").getValue(String.class);
                    String imageUrl = snapshot.child("imageUrl").getValue(String.class);
                    uId = snapshot.child("uId").getValue(String.class);
                    if (firebaseAuth.getCurrentUser().getUid().equals(uId)) {
                        imagAddtoFav.setVisibility(View.GONE);
                    }
                    tvUname.setText(name);
                    tvName.setText(name);
                    tGander.setText(gander);
                    tvAddress.setText(address);
                    tvEmail.setText(email);
                    tvPhone.setText(phone);
                    Picasso.get().load(imageUrl).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbarProfile);
        tvName = findViewById(R.id.tvprofilenameUser);
        tvEmail = findViewById(R.id.tvEmailDetail);
        tvAddress = findViewById(R.id.tvUserAddres);
        tvPhone = findViewById(R.id.tvContact);
        tGander = findViewById(R.id.tvGanderProfile);
        imageView = findViewById(R.id.userProfile);
        tvUname = findViewById(R.id.userprofileName);
        imagAddtoFav = findViewById(R.id.addTofav);
        myRequestCount = findViewById(R.id.myRequestsCount);
        tvmyDonationcount = findViewById(R.id.myDonaitonCount);
        tvBloodGroup = findViewById(R.id.tvBloodGroupProfile);
        dbref = FirebaseDatabase.getInstance().getReference("Users");
        firebaseAuth = FirebaseAuth.getInstance();
        progresDialog = new Dialog(this);
        View mView = getLayoutInflater().inflate(R.layout.progress_dialog_wait, null);
        progresDialog.setContentView(mView);
        progresDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progresDialog.show();
    }


    public void sendSMS(View view) {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(UserProfile.this, Dashboard.class));

    }

    public void AddTofav(View view) {
        final DatabaseReference addtoFavRef = FirebaseDatabase.getInstance().getReference("Favrouts");
        Query ifIsexist = addtoFavRef.child(firebaseAuth.getCurrentUser().getUid()).orderByChild("uId").equalTo(uId);
        final String pushkey = addtoFavRef.push().getKey();
        final MyfavModel myfavModel = new MyfavModel();
        myfavModel.setuId(uId);
        myfavModel.setPushId(pushkey);
        ifIsexist.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    addtoFavRef.child(firebaseAuth.getCurrentUser().getUid()).child(pushkey).setValue(myfavModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                imagAddtoFav.setImageResource(R.drawable.heartfilled);
                                Snackbar.make(imagAddtoFav, "Add to your favrouts", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    imagAddtoFav.setImageResource(R.drawable.heartfilled);
                    Snackbar.make(imagAddtoFav, "Already in you favrouts", Snackbar.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}