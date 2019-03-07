package com.milan.brtshelper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class BusActivity extends AppCompatActivity {

    Bus buses[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus);
        buses = Bus.getBuses(getAssets());
    }
}
