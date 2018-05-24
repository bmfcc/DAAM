package com.iscte.dam;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.estimote.mustard.rx_goodness.rx_requirements_wizard.Requirement;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory;
import com.estimote.proximity_sdk.proximity.EstimoteCloudCredentials;
import com.estimote.proximity_sdk.proximity.ProximityAttachment;
import com.estimote.proximity_sdk.proximity.ProximityObserver;
import com.estimote.proximity_sdk.proximity.ProximityObserverBuilder;
import com.estimote.proximity_sdk.proximity.ProximityZone;

import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ProximityObserver proximityObserver;
    private ProximityObserver.Handler proximityHandler = null;
    private String CHANNEL_ID = "notification_channelID";
    protected String name = "my_package_channel";
    protected String description = "my_package_first_channel";
    protected int importance = NotificationManager.IMPORTANCE_HIGH;
    private String zoneID = null;

    public static final String PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME,0);
        String language = preferences.getString("selected_language", "Default");
        Log.w("TestLanguage", language);

        if(language.equals("Default")){
            selectLanguage();
            finish();
        }else {

            String setupBeacons = preferences.getString("setupBeacons", "Default");
            Log.e("TESTINGGGGGGGG", setupBeacons);

            if(setupBeacons.equals("Default") || setupBeacons.equals("true")) {
                Toast toast = Toast.makeText(getApplicationContext(), "Let's START!", Toast.LENGTH_SHORT);
                toast.show();
                setupBeacons();

                SharedPreferences.Editor editor=preferences.edit();
                editor.putString("setupBeacons","false");
                editor.commit();
            }
        }
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

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_language) {
            // Handle the camera action
            Intent intent = new Intent(this, SelectLanguage.class);
            startActivity(intent);
        } else if (id == R.id.nav_myZone) {
            getMyZone();

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void selectedZooLocation(View view){
        int viewID = view.getId();
        String resourceName = getResources().getResourceEntryName(viewID);
        Log.d("MainAtivitityLog",resourceName);
        Intent intent = new Intent(this, ZoneInfo.class);
        startActivity(intent);
    }

    public void selectLanguage(){
        Intent intent = new Intent(this, SelectLanguage.class);
        startActivity(intent);
    }

    public void goToZooLocation(View view){
        /*Intent intent = new Intent(this, ZoneInfo.class);
        startActivity(intent);*/

        // Create an Intent for the activity you want to start
        //Intent intent = new Intent(this, ZoneInfo.class);
        final Intent notificationIntent = new Intent(this, ZoneInfo.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        /*TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Toast toast = Toast.makeText(getApplicationContext(), "Getting ready", Toast.LENGTH_SHORT);
        toast.show();

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID )
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("ZooZone")
                .setContentText("Bem-vindo ao ZOO!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true);

        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

        mChannel.setDescription(description);
        mChannel.enableVibration(true);
        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        notificationManager.createNotificationChannel(mChannel);


        notificationManager.notify(64647, mBuilder.build());

        */
    }

    public void goToLocationTest (View v){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground))
                .setContentTitle("ZooZone")
                .setContentText("Bem-vindo ao ZOO!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                .setAutoCancel(true);

        final Intent notificationIntent = new Intent(this, ZoneInfo.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        //notificationIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ZoneInfo.class);
        stackBuilder.addNextIntent(notificationIntent);


        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(64647, builder.build());

        zoneID = "foca-comum";

    }

    private void setupBeacons(){
        EstimoteCloudCredentials cloudCredentials =
                new EstimoteCloudCredentials("zoozone-how", "861b9e3dc034aa6c3a7afa2c51220271");

        final Intent notificationIntent = new Intent(this, ZoneInfo.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.putExtra("fromNotification",true);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ZoneInfo.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Toast toast = Toast.makeText(getApplicationContext(), "Getting ready", Toast.LENGTH_SHORT);
        toast.show();

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground))
                .setContentTitle("ZooZone")
                .setContentText("Bem-vindo ao ZOO!")
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
                                Log.e("app", "proximity observer error: " + throwable);
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
                        if(attachment.hasAttachment()){
                            zoo_location = attachment.getPayload().get("beacon_location");
                        }
                        Toast toast = Toast.makeText(getApplicationContext(), "Welcome", Toast.LENGTH_SHORT);
                        toast.show();
                        notificationManager.notify(64647, builder.build());

                        SharedPreferences preferences = getSharedPreferences(PREFS_NAME,0);
                        SharedPreferences.Editor editor=preferences.edit();
                        editor.putString("zoo_location",zoo_location);
                        editor.commit();

                        return null;
                    }
                })
                .withOnExitAction(new Function1<ProximityAttachment, Unit>() {
                    @Override
                    public Unit invoke(ProximityAttachment attachment) {
                        Log.d("beacon", "Bye bye, come visit us again on the 1st floor");
                        Toast toast = Toast.makeText(getApplicationContext(), "Byeee", Toast.LENGTH_SHORT);
                        toast.show();
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
                                Log.d("app", "requirements fulfilled");
                                proximityHandler = proximityObserver.start();
                                return null;
                            }
                        },
                        // onRequirementsMissing
                        new Function1<List<? extends Requirement>, Unit>() {
                            @Override public Unit invoke(List<? extends Requirement> requirements) {
                                Log.e("app", "requirements missing: " + requirements);
                                return null;
                            }
                        },
                        // onError
                        new Function1<Throwable, Unit>() {
                            @Override public Unit invoke(Throwable throwable) {
                                Log.e("app", "requirements error: " + throwable);
                                return null;
                            }
                        });


        Toast toast1 = Toast.makeText(getApplicationContext(), "Everything ready", Toast.LENGTH_SHORT);
        toast1.show();
    }

    private void getMyZone(){
        //Alerta para a Primeira Atividade
        final AlertDialog.Builder dialBuilder1 = new AlertDialog.Builder(this);

        if(zoneID!=null) {
            dialBuilder1.setMessage("You are in " + zoneID + " Zone! Do you wanna know more?")
                    .setTitle("My Zone");
            dialBuilder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    Toast toast = Toast.makeText(getApplicationContext(), "Let's know more", Toast.LENGTH_SHORT);
                    toast.show();
                    dialog.dismiss();
                    //proximityHandler.stop();
                    //proximityHandler=null;
                    //goToZooLocation();
                }
            });
            dialBuilder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                    Toast toast = Toast.makeText(getApplicationContext(), "Oh... OK :´(", Toast.LENGTH_SHORT);
                    toast.show();
                    dialog.dismiss();
                }
            });
        } else{
            dialBuilder1.setMessage("You have not passed any of our zones yet")
                    .setTitle("My Zone");
            dialBuilder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    Toast toast = Toast.makeText(getApplicationContext(), "Let's know more", Toast.LENGTH_SHORT);
                    toast.show();
                    dialog.dismiss();
                }
            });
        }

        AlertDialog dialog = dialBuilder1.create();
        dialog.show();
    }
}
