package com.milan.brtshelper;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class TestTravellingActivity extends AppCompatActivity {
    LocationManager locationManager;
    LocationListener locationListener;
    ArrayList<String> journey;
    Station stations[];
    int no_of_stations;
    ArrayList<String> routeStationNames;

    //temporary
    ListView distance;
    ArrayList<String> al;
    ArrayAdapter<String> arrayAdapter;
    int j = 0, k = 0;
    double tempLatitude, tempLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travelling2);
        //txtLat = (TextView) findViewById(R.id.textview1);

        stations = Station.getStations(getAssets());
        SharedPreferences sharedPreferences;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        sharedPreferences = getSharedPreferences("resources", MODE_PRIVATE);
        journey = (ArrayList<String>) getIntent().getSerializableExtra("journeyNodes");
        routeStationNames = (ArrayList<String>) getIntent().getSerializableExtra("routeStationNames");
        no_of_stations = sharedPreferences.getInt("no_of_stations", 0);
        Log.i("routeSize", Integer.toString((routeStationNames.size())));

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                int temp = 0;
                distance = findViewById(R.id.progress);
                al = new ArrayList<String>();
                Log.i("routeSize", Integer.toString((routeStationNames.size())));
                for (int j = 0; j < routeStationNames.size(); j++) {
                    for (int i = 0; i < no_of_stations; i++) {

                        Log.i("station", stations[i].name + " " + routeStationNames.get(j));
                        if ((stations[i].name).equalsIgnoreCase(routeStationNames.get(j))) {
                            //Log.i("distance",Float.toString(distFrom(stations[i].latitude,stations[i].longnitude, location.getLatitude(),location.getLongitude())));
                            for (int l = 0; l < no_of_stations; l++) {
                                if ((stations[l].name).equalsIgnoreCase(routeStationNames.get(j))) {
                                    tempLatitude = stations[l].latitude;
                                    tempLongitude = stations[l].longnitude;
                                }
                            }
                            if (distFrom(tempLatitude, tempLongitude, location.getLatitude(), location.getLongitude()) > 10) {

                                al.add(k, routeStationNames.get(j) + "\n" + String.format("%,.2f", ((distFrom(stations[i].latitude, stations[i].longnitude, location.getLatitude(), location.getLongitude()) / 1000))) + "km");
                                Log.i("locationchanged", stations[i].name + "\n" + distFrom(stations[i].latitude, stations[i].longnitude, location.getLatitude(), location.getLongitude()));
                                k++;
                                //j++;
                                break;
                            } else {
                                routeStationNames.remove(k);
                                if (routeStationNames.size() == 0) {
                                    TextView textView = findViewById(R.id.textView4);
                                    textView.setText("YOU HAVE REACHED TO YOUR DESTINATION.");
                                }
                                break;
                            }

                        }
                        if (j == routeStationNames.size()) {
                            j = 0;
                            k = 0;
                            break;
                        }
                    }
                }

                arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, al);
                distance.setAdapter(arrayAdapter);

            }


            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }



    }
    public static float distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }
    }
