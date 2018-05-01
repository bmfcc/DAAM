package com.iscte.dam;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Notificação
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "testing_notification")
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("ZooZone")
                .setContentText("Bem-vindo ao ZOO!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        final AlertDialog.Builder notBuilder = new AlertDialog.Builder(this);
        notBuilder.setMessage("Welcome to the XXX Zone! Do you wanna know more?")
                .setTitle("XXX Zone");
        notBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Toast toast = Toast.makeText(getApplicationContext(), "Let's know more", Toast.LENGTH_SHORT);
                toast.show();
                dialog.dismiss();
                goToZooLocation();
            }
        });
        notBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                Toast toast = Toast.makeText(getApplicationContext(), "Oh... OK :´(", Toast.LENGTH_SHORT);
                toast.show();
                dialog.dismiss();
            }
        });


        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.SELECTED_LANGUAGE);

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.textView);
        textView.setText(message);

        EstimoteCloudCredentials cloudCredentials =
                new EstimoteCloudCredentials("zoozone-how", "861b9e3dc034aa6c3a7afa2c51220271");

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

                        AlertDialog dialog = notBuilder.create();
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
        Toast toast = Toast.makeText(getApplicationContext(), "Everything ready", Toast.LENGTH_SHORT);
        toast.show();

        RequirementsWizardFactory
                .createEstimoteRequirementsWizard()
                .fulfillRequirements(this,
                        // onRequirementsFulfilled
                        new Function0<Unit>() {
                            @Override public Unit invoke() {
                                Log.d("app", "requirements fulfilled");
                                proximityObserver.start();
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

}
