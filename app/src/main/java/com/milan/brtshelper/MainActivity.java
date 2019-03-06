package com.milan.brtshelper;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {
    android.widget.SearchView searchviewmain;
    android.widget.SearchView searchviewdestmain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        searchviewdestmain = (android.widget.SearchView) findViewById(R.id.destination);
        searchviewmain = findViewById(R.id.source);
        searchviewdestmain.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, v ,"src_shared_searchbar_main");
                Intent i = new Intent(MainActivity.this,SearchSouceFromMain.class);
                startActivityForResult(i,2);
            }
        });

        /*searchviewmain.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, v ,"src_shared_searchbar_main");
                Intent i = new Intent(MainActivity.this,SearchSouceFromMain.class);
                startActivityForResult(i,1);
            }
        });*/
        /*EditText editt = findViewById(R.id.editText);
        editt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, v ,"src_shared_searchbar_main");
                Intent i = new Intent(MainActivity.this, SearchSouceFromMain.class);
                startActivityForResult(i,1);
                return false;
            }
        });*/

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
        else{
            if (resultcode == RESULT_OK) {
              Log.i("hghgg", "Hardik1111111111111111");
                searchviewmain = (android.widget.SearchView) findViewById(R.id.source);


              String temp = (String) data.getExtras().getString("passsrc");
                Log.i("11hghgg", temp);
                searchviewmain.setIconifiedByDefault(true);
                searchviewmain.setFocusable(true);
                searchviewmain.setIconified(false);



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
}
