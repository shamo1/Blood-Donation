package com.example.blooddonationkotli.Model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.blooddonationkotli.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomWindowAdaptor implements GoogleMap.InfoWindowAdapter {
    Context context;
    View mWindowView;

    public CustomWindowAdaptor(Context context) {
        this.context = context;
        this.mWindowView = LayoutInflater.from(context).inflate(R.layout.custom_infowindow_marker, null);
    }

    void rendoWindowText(Marker marker, View mView) {

        TextView textView = mView.findViewById(R.id.infowindowName);
        String title = marker.getTitle();
        if (!title.equals("")) {
            textView.setText(title);
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        rendoWindowText(marker, mWindowView);
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        rendoWindowText(marker, mWindowView);
        return null;
    }
}
