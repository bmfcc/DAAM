package com.iscte.zoozone;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.estimote.mustard.rx_goodness.rx_requirements_wizard.Requirement;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory;
import com.estimote.proximity_sdk.proximity.EstimoteCloudCredentials;
import com.estimote.proximity_sdk.proximity.ProximityAttachment;
import com.estimote.proximity_sdk.proximity.ProximityObserver;
import com.estimote.proximity_sdk.proximity.ProximityObserverBuilder;
import com.estimote.proximity_sdk.proximity.ProximityZone;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iscte.zoozone.models.History;
import com.iscte.zoozone.models.Menu;
import com.iscte.zoozone.models.Messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import me.relex.circleindicator.CircleIndicator;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ProximityObserver proximityObserver;
    private ProximityObserver.Handler proximityHandler = null;
    private String CHANNEL_ID = "notification_channelID";
    protected String name = "my_package_channel";
    private String zoneID = null;
    private String language;

    private String zoneNotFound = "You have not passed any of our zones yet";
    private String zoneFound = "Your last zone was: zoneID! Do you wanna know more?";
    private String msgZoneTitle = "My Zone";
    private String notificationMsg = "Welcome to Zone zoneID!";

    private DatabaseReference dbImagesRef;
    private DatabaseReference dbHistoryRef;
    private DatabaseReference dbMenuRef;
    private DatabaseReference dbMessagesRef;

    private ArrayList<String> slidingImages;
    private static ViewPager mPager;
    private static int currentPage = 0;

    public static final String PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

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
        language = preferences.getString("selected_language", "Default");

        if(language.equals("Default")){
            selectLanguage();
            finish();
        }else {
            setSlider();

            String setupBeacons = SetupBeacons.getInstance().getSetupBeacons();

            if(setupBeacons.equals("True")) {
                setupBeacons();

                SetupBeacons.getInstance().setSetupBeacons("False");
            }

        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_language) {
            // Handle the camera action
            selectLanguage();
            finish();
        } else if (id == R.id.nav_myZone) {
            getMyZone();

        } else if (id == R.id.nav_map) {
            Intent intent = new Intent(this, MapActivity.class);
            intent.putExtra("mapTitle",item.getTitle());
            startActivity(intent);
        } else if (id == R.id.nav_news) {
            Intent intent = new Intent(this, NewsRV.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_report) {

        } else if (id == R.id.nav_classify) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void selectLanguage(){
        Intent intent = new Intent(this, SelectLanguage.class);
        startActivity(intent);
    }

    private void setupBeacons(){
        EstimoteCloudCredentials cloudCredentials =
                new EstimoteCloudCredentials("zoozone-how", "861b9e3dc034aa6c3a7afa2c51220271");

        final Intent notificationIntent = new Intent(this, ZoneInfo.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ZoneInfo.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground))
                .setContentTitle("ZooZone")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                .setAutoCancel(true);

        builder.setContentIntent(resultPendingIntent);

        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        this.proximityObserver =
                new ProximityObserverBuilder(getApplicationContext(), cloudCredentials)
                        .withOnErrorAction(new Function1<Throwable, Unit>() {
                            @Override
                            public Unit invoke(Throwable throwable) {
                                Log.e("app_beacons", "proximity observer error: " + throwable);
                                return null;
                            }
                        })
                        .withBalancedPowerMode()
                        .withEstimoteSecureMonitoringDisabled()
                        .withTelemetryReportingDisabled()
                        .build();

        ProximityZone zone1 = this.proximityObserver.zoneBuilder()
                .forAttachmentKeyAndValue("beacon_from", "zoo")
                .inNearRange()
                .withOnEnterAction(new Function1<ProximityAttachment, Unit>() {
                    @Override
                    public Unit invoke(ProximityAttachment attachment) {
                        String zoo_location = "";
                        String zoo_location_desc = "";
                        if(attachment.hasAttachment()){
                            zoo_location = attachment.getPayload().get("beacon_location");
                            zoo_location_desc = attachment.getPayload().get("beacon_location_desc");
                        }

                        builder.setContentText(notificationMsg.replace("zoneID",zoo_location_desc));
                        notificationManager.notify(64647, builder.build());

                        SharedPreferences preferences = getSharedPreferences(PREFS_NAME,0);
                        SharedPreferences.Editor editor=preferences.edit();
                        editor.putString("zoo_location",zoo_location);
                        editor.putString("zoo_location_desc",zoo_location_desc);
                        editor.commit();

                        return null;
                    }
                })
                .withOnExitAction(new Function1<ProximityAttachment, Unit>() {
                    @Override
                    public Unit invoke(ProximityAttachment attachment) {
                        return null;
                    }
                })
                .create();
        this.proximityObserver.addProximityZone(zone1);

        RequirementsWizardFactory
                .createEstimoteRequirementsWizard()
                .fulfillRequirements(this,
                        // onRequirementsFulfilled
                        new Function0<Unit>() {
                            @Override public Unit invoke() {
                                Log.d("app_beacons", "requirements fulfilled");
                                proximityHandler = proximityObserver.start();
                                return null;
                            }
                        },
                        // onRequirementsMissing
                        new Function1<List<? extends Requirement>, Unit>() {
                            @Override public Unit invoke(List<? extends Requirement> requirements) {
                                Log.e("app_beacons", "requirements missing: " + requirements);
                                return null;
                            }
                        },
                        // onError
                        new Function1<Throwable, Unit>() {
                            @Override public Unit invoke(Throwable throwable) {
                                Log.e("app_beacons", "requirements error: " + throwable);
                                return null;
                            }
                        });
    }

    private void getMyZone(){
        final AlertDialog.Builder dialBuilder1 = new AlertDialog.Builder(this);
        final Intent zoneIntent = new Intent(this, ZoneInfo.class);

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME,0);
        zoneID =  preferences.getString("zoo_location", "Default");
        String zone_desc = preferences.getString("zoo_location_desc","Default");

        if(zone_desc!="Default") {
            dialBuilder1.setMessage(zoneFound.replace("zoneID",zone_desc))
                    .setTitle(msgZoneTitle);
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
            dialBuilder1.setMessage(zoneNotFound)
                    .setTitle(msgZoneTitle);
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

    private void setSlider() {

        getDBInfo();

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if(slidingImages!=null) {
                    if (currentPage == slidingImages.size()) {
                        currentPage = 0;
                    }
                    mPager.setCurrentItem(currentPage++, true);
                }
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 5000, 10000);
    }

    private void getDBInfo(){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbImagesRef = database.getReference("SlidingImages");

        dbHistoryRef = database.getReference("History").child(language);

        dbMenuRef = database.getReference("Menu").child(language);

        dbMessagesRef = database.getReference("Messages").child(language);

        ValueEventListener postListenerImages = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                slidingImages = (ArrayList<String>) dataSnapshot.getValue();

                mPager = (ViewPager) findViewById(R.id.pager);
                mPager.setAdapter(new Adapter(Main2Activity.this,slidingImages));
                CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
                indicator.setViewPager(mPager);
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("GetBDValueError", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        dbImagesRef.addListenerForSingleValueEvent(postListenerImages);

        ValueEventListener postListenerHistory = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                TextView history = findViewById(R.id.textView_History);
                TextView historyTitle = findViewById(R.id.tv_main_History);

                History hist = dataSnapshot.getValue(History.class);
                historyTitle.setText(hist.getTitle());
                history.setText(hist.getText());
                history.setMovementMethod(new ScrollingMovementMethod());

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("GetBDValueError", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        dbHistoryRef.addListenerForSingleValueEvent(postListenerHistory);

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
                menuLayout.findItem(R.id.nav_map).setTitle(menu.getMap());

                msgZoneTitle = menu.getGetMyZone();

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

        ValueEventListener postListenerMessages = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                Messages msg = dataSnapshot.getValue(Messages.class);
                zoneNotFound = msg.getZoneNotFound();
                zoneFound = msg.getZoneFound();
                notificationMsg = msg.getNotificationMsg();

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("GetBDValueError", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        dbMessagesRef.addListenerForSingleValueEvent(postListenerMessages);
    }
}
