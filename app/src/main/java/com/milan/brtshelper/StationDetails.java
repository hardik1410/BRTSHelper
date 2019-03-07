package com.milan.brtshelper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class StationDetails extends AppCompatActivity {

    ArrayList<String> stationNames;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_details);
        stationNames=Station.getStationNames();

    }
}
