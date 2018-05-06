package com.iscte.dam;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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

public class HomeActivity extends AppCompatActivity {

    private ProximityObserver proximityObserver;
    private ProximityObserver.Handler proximityHandler = null;
    private String CHANNEL_ID = "notification_channelID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Channel for Notifications applied only on API's +26
        createNotificationChannel();

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.SELECTED_LANGUAGE);

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.textView);
        textView.setText(message);

        setupBeacons();

        Toast toast = Toast.makeText(getApplicationContext(), "Everything ready", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onPause(){
        super.onPause();

        Toast toast = Toast.makeText(getApplicationContext(), "1st Activity Pausing", Toast.LENGTH_SHORT);
        toast.show();

        //proximityHandler.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Toast toast = Toast.makeText(getApplicationContext(), "1st Activity Destroy", Toast.LENGTH_SHORT);
        toast.show();

        //proximityHandler.stop();
    }

    @Override
    protected void onStop() {
        super.onStop();

        Toast toast = Toast.makeText(getApplicationContext(), "1st Activity Stoping", Toast.LENGTH_SHORT);
        toast.show();

    }

    @Override
    protected void onResume(){
        super.onResume();

        Toast toast = Toast.makeText(getApplicationContext(), "1st Activity Resuming", Toast.LENGTH_SHORT);
        toast.show();

    }

    private void setupBeacons(){
        EstimoteCloudCredentials cloudCredentials =
                new EstimoteCloudCredentials("zoozone-how", "861b9e3dc034aa6c3a7afa2c51220271");

        // Create an Intent for the activity you want to start
        Intent intent = new Intent(this, HomeActivity2.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID )
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("ZooZone")
                .setContentText("Bem-vindo ao ZOO!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        //Alerta para a Primeira Atividade
        final AlertDialog.Builder dialBuilder1 = new AlertDialog.Builder(this);
        dialBuilder1.setMessage("Welcome to the XXX Zone! Do you wanna know more?")
                .setTitle("XXX Zone");
        dialBuilder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Toast toast = Toast.makeText(getApplicationContext(), "Let's know more", Toast.LENGTH_SHORT);
                toast.show();
                dialog.dismiss();
                //proximityHandler.stop();
                //proximityHandler=null;
                goToZooLocation();
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
                .forAttachmentKeyAndValue("floor", "1st")
                .inNearRange()
                .withOnEnterAction(new Function1<ProximityAttachment, Unit>() {
                    @Override
                    public Unit invoke(ProximityAttachment attachment) {
                        Log.d("beacon", "Welcome to the 1st floors");
                        Toast toast = Toast.makeText(getApplicationContext(), "Welcome", Toast.LENGTH_SHORT);
                        toast.show();
                        notificationManager.notify(64647, mBuilder.build());

                        AlertDialog dialog = dialBuilder1.create();
                        dialog.show();
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

    }

    public void selectedZooLocation(View view){
        int viewID = view.getId();
        String resourceName = getResources().getResourceEntryName(viewID);
        Log.d("MainAtivitityLog",resourceName);
        Intent intent = new Intent(this, HomeActivity2.class);
        startActivity(intent);
    }

    public void goToZooLocation(){
        Intent intent = new Intent(this, HomeActivity2.class);
        startActivity(intent);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //CharSequence name = getString(R.string.channel_name);
            //String description = getString(R.string.channel_description);
            CharSequence name = "channel_name";
            String description = "channel_description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            //NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            NotificationChannel channel = new NotificationChannel("channeID", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
