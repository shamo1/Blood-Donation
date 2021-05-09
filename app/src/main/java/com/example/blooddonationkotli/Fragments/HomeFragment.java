package com.example.blooddonationkotli.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.blooddonationkotli.Activities.AvailableBlood;
import com.example.blooddonationkotli.Activities.BloodRequests;
import com.example.blooddonationkotli.Activities.DonationPost;
import com.example.blooddonationkotli.Activities.Reminder;
import com.example.blooddonationkotli.R;
import com.example.blooddonationkotli.Utils.FirebaseOffline;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private static FusedLocationProviderClient fusedLocationProviderClient;
    CardView cardviewFindBlood, donateBlood, availableBlood, cadrViewReminder;
    TextView tvBloodRequests, tvDonationInterest;
    DatabaseReference refrence, interestRef;
    String city;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseOffline.getSync();
        getLocaiton(getContext());
        initViews(view);
        cardviewFindBlood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), BloodRequests.class));
            }
        });
        donateBlood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), DonationPost.class));
            }
        });
        availableBlood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AvailableBlood.class));
            }
        });
        cadrViewReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), Reminder.class));
            }
        });
    }


    private void initViews(View view) {
        cardviewFindBlood = view.findViewById(R.id.carviewFind);
        donateBlood = view.findViewById(R.id.cardViewdonate);
        availableBlood = view.findViewById(R.id.carviewAvailable);
        tvBloodRequests = view.findViewById(R.id.tvpeopleRequests);
        tvDonationInterest = view.findViewById(R.id.tvDonationInterest);
        cadrViewReminder = view.findViewById(R.id.cardviewReminder);
        refrence = FirebaseDatabase.getInstance().getReference("Blood Requests");
        interestRef = FirebaseDatabase.getInstance().getReference("Donations");
    }

    @SuppressLint("MissingPermission")
    public void getLocaiton(final Context context) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {

                    try {
                        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        String _addres = addresses.get(0).getAddressLine(0);
                        city = addresses.get(0).getLocality();
                        getReqeustCount(city);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getReqeustCount(String city) {
        Query query = refrence.orderByChild("city").equalTo(city);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    long count = snapshot.getChildrenCount();
                    tvBloodRequests.setText(String.valueOf(count));
                } else {
                    tvBloodRequests.setText("-");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query donQuery = interestRef.orderByChild("city").equalTo(city);
        donQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    long count = snapshot.getChildrenCount();
                    tvDonationInterest.setText(String.valueOf(count));
                } else {
                    tvDonationInterest.setText("-");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}