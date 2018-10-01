package com.iscte.zoozone;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.iscte.zoozone.models.Menu;
import com.iscte.zoozone.models.News;

import java.util.ArrayList;

public class NewsRV extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static final String PREFS_NAME = "MyPrefsFile";
    private String zoneID = null;
    private ArrayList<News> newsList;
    private DatabaseReference dbRef;
    private DatabaseReference dbMenuRef;

    private String language;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_news);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.textColorPrimary));
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.textColorPrimary));
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.textColorPrimary));
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME,0);
        language = preferences.getString("selected_language","EN");

        mRecyclerView = (RecyclerView) findViewById(R.id.news_RV);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        if(mRecyclerView==null) {
            Log.e("app", "NewsRV - mRecyclerView - NULL");
        }

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        getDBInfo();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_language) {
            Intent intent = new Intent(this, SelectLanguage.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_myZone) {
            getMyZone();

        } else if (id == R.id.nav_home) {
            Intent intent = new Intent(this, Main2Activity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_map) {
            Intent intent = new Intent(this, MapActivity.class);
            intent.putExtra("mapTitle",item.getTitle());
            startActivity(intent);
        } else if (id == R.id.nav_report) {

        } else if (id == R.id.nav_classify) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getMyZone(){
        //Alerta para a Primeira Atividade
        final AlertDialog.Builder dialBuilder1 = new AlertDialog.Builder(this);
        final Intent zoneIntent = new Intent(this, ZoneInfo.class);

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME,0);
        zoneID =  preferences.getString("zoo_location", "Default");

        if(zoneID!="Default") {
            dialBuilder1.setMessage("Your last zone was: " + zoneID + "! Do you wanna know more?")
                    .setTitle("My Zone");
            dialBuilder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    dialog.dismiss();
                    startActivity(zoneIntent);
                }
            });
            dialBuilder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                    dialog.dismiss();
                }
            });
        } else{
            dialBuilder1.setMessage("You have not passed any of our zones yet")
                    .setTitle("My Zone");
            dialBuilder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    dialog.dismiss();
                }
            });
        }

        AlertDialog dialog = dialBuilder1.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void getDBInfo(){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("News").child(language+"/"+"newsList");
        dbMenuRef = database.getReference("Menu").child(language);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                GenericTypeIndicator<ArrayList<News>> t = new GenericTypeIndicator<ArrayList<News>>() {};
                newsList = dataSnapshot.getValue(t);
                mAdapter = new RecViewAdapter(newsList);
                mRecyclerView.setAdapter(mAdapter);
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("GetBDValueError", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        dbRef.addListenerForSingleValueEvent(postListener);

        ValueEventListener postListenerMenu = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                NavigationView nav = findViewById(R.id.nav_view);
                android.view.Menu menuLayout = nav.getMenu();

                Menu menu = dataSnapshot.getValue(Menu.class);

                menuLayout.findItem(R.id.nav_home).setTitle(menu.getHome());
                menuLayout.findItem(R.id.nav_language).setTitle(menu.getLanguage());
                menuLayout.findItem(R.id.nav_myZone).setTitle(menu.getGetMyZone());
                menuLayout.findItem(R.id.nav_news).setTitle(menu.getNews());
                menuLayout.findItem(R.id.nav_communicate).setTitle(menu.getCommunicate());
                menuLayout.findItem(R.id.nav_classify).setTitle(menu.getClassify());
                menuLayout.findItem(R.id.nav_report).setTitle(menu.getReport());
                ((TextView)findViewById(R.id.tv_news)).setText(menu.getNews());

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("GetBDValueError", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        dbMenuRef.addListenerForSingleValueEvent(postListenerMenu);
    }
}
