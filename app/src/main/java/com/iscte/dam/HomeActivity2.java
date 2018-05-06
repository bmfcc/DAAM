package com.iscte.dam;

/**
 * Created by JD on 09-03-2018.
 */

import android.app.AlertDialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.mustard.rx_goodness.rx_requirements_wizard.Requirement;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory;
import com.estimote.proximity_sdk.proximity.EstimoteCloudCredentials;
import com.estimote.proximity_sdk.proximity.ProximityAttachment;
import com.estimote.proximity_sdk.proximity.ProximityObserver;
import com.estimote.proximity_sdk.proximity.ProximityObserverBuilder;
import com.estimote.proximity_sdk.proximity.ProximityZone;

import java.time.LocalDate;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;


public class HomeActivity2 extends AppCompatActivity {

    private ProximityObserver proximityObserver;
    private ProximityObserver.Handler proximityHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        dostuff();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast toast = Toast.makeText(getApplicationContext(), "DESTROYYYYYYYY", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onResume(){
        super.onResume();

        Toast toast = Toast.makeText(getApplicationContext(), "2nd Activity Resuming", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void dostuff(){
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.SELECTED_LANGUAGE);
    }

    private void setupBeacons(){
        EstimoteCloudCredentials cloudCredentials =
                new EstimoteCloudCredentials("zoozone-how", "861b9e3dc034aa6c3a7afa2c51220271");

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "testing_notification")
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("ZooZone")
                .setContentText("Bem-vindo ao ZOO!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});

        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        final AlertDialog.Builder diagBuilder = new AlertDialog.Builder(this);
        diagBuilder.setMessage("Welcome to the XXX Zone! Do you wanna know more?")
                .setTitle("XXX Zone");
        diagBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Toast toast = Toast.makeText(getApplicationContext(), "Let's know more", Toast.LENGTH_SHORT);
                toast.show();
                dialog.dismiss();
                proximityHandler.stop();
                proximityHandler = null;
                goToZooLocation();
            }
        });
        diagBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
                        notificationManager.notify(64647,mBuilder.build());

                        AlertDialog dialog = diagBuilder.create();
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

    public void goToZooLocation(){
        Intent intent = new Intent(this, HomeActivity2.class);
        startActivity(intent);
    }

}