package com.example.blooddonationkotli.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.blooddonationkotli.R;
import com.example.blooddonationkotli.Recievers.NetworkReciever;

public class RecieverTest extends AppCompatActivity {
    NetworkReciever reciever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reciever_test);
    }

}