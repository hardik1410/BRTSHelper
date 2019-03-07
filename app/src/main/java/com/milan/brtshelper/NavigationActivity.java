package com.milan.brtshelper;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;

import static android.widget.Toast.LENGTH_LONG;

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private android.support.v7.widget.Toolbar mToolbar;
    private DrawerLayout mDrawer;
    private FirebaseFirestore db;
    private String uid;
    int no_of_stations=40, no_of_bus=4, no_of_routes=4;
    Station stations[];

    Bus b[];
    Route r[];
    Integer routesPossible[]=new Integer[5000], tripsPossible[]=new Integer[5000];int countOfRoutes=0, countOfTrips=0;
    int min=0;
    ArrayList<String> tempDataRepresentation = new ArrayList<String>();
    android.widget.SearchView searchviewmain;
    android.widget.SearchView searchviewdestmain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        stations=Station.getStations(this.getAssets());
        r=Route.getRoutes(getAssets());
        b = Bus.getBuses(getAssets());
        SharedPreferences sharedPreferences = getSharedPreferences("resources", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("no_of_stations", no_of_stations);
        editor.putInt("no_of_bus", no_of_bus);
        editor.putInt("no_of_routes", no_of_routes);
        editor.commit();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getPhoneNumber().toString().trim();

        } else {
            Intent i =new Intent(NavigationActivity.this,LoginActivity.class);
            startActivity(i);
        }
        Toast.makeText(NavigationActivity.this,uid, LENGTH_LONG).show();
        setupToolbar();
        setupNavDrawermenu(uid);



        /*EditText editt = findViewById(R.id.editText);
        editt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(NavigationActivity.this, v ,"src_shared_searchbar_main");
                Intent i = new Intent(NavigationActivity.this, SearchSouceFromMain.class);
                startActivityForResult(i,1);
                return false;
            }
        });*/

    }

    int addMilitaryTime(int a, int b)
    {
        int a_hr = a/100, b_hr = b/100, a_min = a%100, b_min = b%100;
        a_min+=b_min;
        if(a_min>60)
        {
            a_min%=60;
            a_hr=(a_hr+1)%24;
        }
        a_hr+=b_hr;
        a_hr%=24;
        a=a_hr*100+ a_min;
        Log.i("Timeaaaaa", Integer.toString(a));
        return a;
    }

    int d=1;String mySrc=null;

    int srcNode, destNode;
    SearchView destTextView;
    SearchView sourceTextView;
    ArrayList <Bus> busesAvailable = new ArrayList<Bus>();
    ArrayList <String> routes = new ArrayList<String>();

    ListView listView;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    ArrayList<String> routeStationNames=new ArrayList<>();


    public void destEntered(View view) {
        busesAvailable.clear();
        tempDataRepresentation.clear();
        destTextView = (SearchView) findViewById(R.id.destination);
        sourceTextView = (SearchView) findViewById(R.id.source);
        Log.i("destination",destTextView.getQuery().toString());
        Log.i("source",sourceTextView.getQuery().toString());


        mySrc=(sourceTextView.getQuery().toString().trim().length() > 0)?(sourceTextView.getQuery().toString()):stations[min].name;

        if (destTextView.getQuery().toString().trim().length() > 0) {
            /*for (int i = 0; i < no_of_stations; i++) {
                Log.i("Dest index:", destTextView.getText().toString());
                if (stations[i].name.toString().equals(destTextView.getText().toString())) {
                    d = i;
                    Log.i("Dest index:", Integer.toString(d));
                }
            }*/

            for(int i=0;i<no_of_routes;i++)
            {
                d=1;
                int currentHr = (Calendar.getInstance().getTime().getHours())%24;
                int currentMin = (Calendar.getInstance().getTime().getMinutes())%60;
                int currentTime = currentHr*100 + currentMin;
                int j;
                for(j=0;j<r[i].no_of_nodes;j++) {
                    if (r[i].nodes[j].equals(mySrc)) {
                        srcNode = j;
                        d = 0;
                        break;
                    }
                }
                for(;j<r[i].no_of_nodes;j++)
                {
                    if(d==0 && r[i].nodes[j].equals(destTextView.getQuery().toString()))
                    {
                        Log.i("mysrc",mySrc);
                        for(int k=0;k<no_of_bus;k++)
                        {
                            if (b[k].routeId == r[i].routeId)
                            {

                                for(int l=0;l<b[k].no_of_trips;l++)
                                {
                                    Log.i("mysrc",mySrc);
                                    if(addMilitaryTime(b[k].arr_at_src[l],r[b[k].routeId].offset[srcNode])>currentTime) {
                                        busesAvailable.add(b[k]);
                                        Log.i("mysrc",mySrc);
                                        routesPossible[countOfRoutes++] = i;
                                        tripsPossible[countOfTrips++] = l;
                                        tempDataRepresentation.add("Bus number: "+b[k].busNumber+"\nArrival: "+b[k].arr_at_src[l]);
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    }
                }
            }

        }


        for (int i = 0; i < busesAvailable.size(); i++)
            Log.i("Bus no: ", (busesAvailable.get(i).busNumber));
        final ListView listView = (ListView) findViewById(R.id.buses);
        ArrayAdapter<Bus> arrayAdapter=new ArrayAdapter <Bus>(NavigationActivity.this, android.R.layout.simple_list_item_1, busesAvailable);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            public void onItemClick(AdapterView<?> parent, View clickView,
                                    int position, long id) {
                routes.clear();
                routeStationNames.clear();
                int i;
                for(i=0;i<r[busesAvailable.get(position).routeId].no_of_nodes;i++)
                    if(r[busesAvailable.get(position).routeId].nodes[i].equals(mySrc))
                        break;
                while (!r[busesAvailable.get(position).routeId].nodes[i].equals(destTextView.getQuery().toString()) &&i<33)
                {
                    routes.add(r[busesAvailable.get(position).routeId].nodes[i]+"\nArrival: "+addMilitaryTime(busesAvailable.get(position).arr_at_src[tripsPossible[position]],r[busesAvailable.get(position).routeId].offset[i]));
                    routeStationNames.add(r[busesAvailable.get(position).routeId].nodes[i]);
                    i++;
                }
                routeStationNames.add(r[busesAvailable.get(position).routeId].nodes[i]);
                routes.add(r[busesAvailable.get(position).routeId].nodes[i]+"\nArrival: "+addMilitaryTime(busesAvailable.get(position).arr_at_src[tripsPossible[position]],r[busesAvailable.get(position).routeId].offset[i]));
                i++;
                /*ListView listofstation=(ListView) findViewById(R.id.route);
                ArrayAdapter<String> arrayAdapter1=new ArrayAdapter<String>(MapsActivity.this, android.R.layout.simple_list_item_1, routes);
                listofstation.setAdapter(arrayAdapter1);*/
                SharedPreferences sharedPreferences = getSharedPreferences("resources", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                LinkedHashSet<String> routesSet = new LinkedHashSet<String>(routes);
                LinkedHashSet<String> routeStaions=new LinkedHashSet<>(routeStationNames);
                editor.putStringSet("journey", routesSet);
                editor.putStringSet("routeStationNames", routeStaions);
                editor.commit();
                Intent intent = new Intent(NavigationActivity.this, JourneyActivity.class);
                intent.putExtra("journeyNodes", routes);
                intent.putExtra("routeStationNames", routeStationNames);
                NavigationActivity.this.startActivity(intent);
            }

        });




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

    private void setupNavDrawermenu(String uid) {
        NavigationView navview = (NavigationView) findViewById(R.id.navviewmain);
        View headerview = navview.getHeaderView(0);
        TextView navheaderid = (TextView) headerview.findViewById(R.id.phonenumberuser);
        navheaderid.setText(uid);
        navview.setNavigationItemSelectedListener(this);
    }

    private void setupToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("BRTS Helper");
        mDrawer = (DrawerLayout) findViewById(R.id.drawerlayout);


        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                NavigationActivity.this,
                mDrawer,
                mToolbar,
                R.string.drawer_open,
                R.string.drawer_close
        );

        mDrawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }



    @Override
    protected void onActivityResult(int requestcode, int resultcode, Intent data) {
        Log.i("hghgg", "Hardik");
        if (requestcode == 2) {
            if (resultcode == RESULT_OK) {
                Log.i("hghgg", "Hardik222222222222222");
                searchviewdestmain = (android.widget.SearchView) findViewById(R.id.destination);
                String temp = (String) data.getExtras().getString("passsrc");
                Log.i("112222222", temp);


                searchviewdestmain.setIconifiedByDefault(true);
                searchviewdestmain.setFocusable(true);
                searchviewdestmain.setIconified(false);
//                searchviewmain.requestFocus();
                searchviewdestmain.requestFocusFromTouch();
                searchviewdestmain.setQuery(temp, false);
                searchviewdestmain.clearFocus();

            }
        }
        if (requestcode == 1) {
            if (resultcode == RESULT_OK) {
                Log.i("hghgg", "Hardik1111111111111111");
                searchviewmain = (android.widget.SearchView) findViewById(R.id.source);
//
//
                String temp = (String) data.getExtras().getString("passsrc");
               Log.i("11hghgg", temp);
                searchviewmain.setIconifiedByDefault(true);
                searchviewmain.setFocusable(true);
                searchviewmain.setIconified(false);

                /*EditText editt = findViewById(R.id.editText);
                editt.setText(temp,EditText.BufferType.EDITABLE);
                editt.clearFocus();*/

               searchviewmain.requestFocus();
                searchviewmain.requestFocusFromTouch();
                searchviewmain.setQuery(temp, false);
                searchviewmain.clearFocus();

            }

        }

    }




    public void onmainButtonclicked (View view){

        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    public void onDestinationMainClicked(View view){

    }


    public void onSearchbar1click(View view){

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, view ,"src_shared_searchbar_main");
        Intent i = new Intent(this, SearchSouceFromMain.class);
        startActivityForResult(i,1);

    }
    public void onSearchbar2click(View view){

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, view ,"src_shared_searchbar_main");
        Intent i = new Intent(this, SearchSouceFromMain.class);
        startActivityForResult(i,2);

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        String itemname = (String) menuItem.getTitle();

        closeDrawer();
        Intent i;

        switch (menuItem.getItemId()){
            case   R.id.navprofile:


                DocumentReference docRef = db.collection("users").document(uid);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                //Log.d(TAG, "DocumentSnapshot data: " + document.getString("userfname"));

                                if (document.getString("userfname").contains("null")) {

                                    Intent newi = new Intent(NavigationActivity.this, ProfilePageActivity.class);
                                    startActivity(newi);
                                } else {
                                    Intent newi = new Intent(NavigationActivity.this, ProfileDisplayActivity.class);
                                    startActivity(newi);
                                }
                            } else {
                                Log.d("switchcaseexp", "get failed with ", task.getException());
                            }
                        }
                    }
                });



                break;
            case R.id.navmap:
                i = new Intent(NavigationActivity.this, NavigationMapActivity.class);
                startActivity(i);
                break;
            case R.id.navbuslist:
                i = new Intent(NavigationActivity.this, BusListActivity.class);
                startActivity(i);
                break;

            case R.id.navstationlist:
                i = new Intent(NavigationActivity.this, StationListActivity.class);
                startActivity(i);
                break;

            case R.id.navcontactus:
                i = new Intent(NavigationActivity.this, ContactUsActivity.class);
                startActivity(i);
                break;

            case R.id.userlogout:
                FirebaseAuth.getInstance().signOut();
                i = new Intent(NavigationActivity.this , LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
        }



        return false;
    }

    private void closeDrawer() {
        mDrawer.closeDrawer(GravityCompat.START);
    }

    public void onBackPressed(){
        if (mDrawer.isDrawerOpen(GravityCompat.START)){
            closeDrawer();
        }
        else {
            super.onBackPressed();
        }
    }
}
