package com.iscte.dam;

/**
 * Created by JD on 09-03-2018.
 */

import android.app.AlertDialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.estimote.mustard.rx_goodness.rx_requirements_wizard.Requirement;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory;
import com.estimote.proximity_sdk.proximity.EstimoteCloudCredentials;
import com.estimote.proximity_sdk.proximity.ProximityAttachment;
import com.estimote.proximity_sdk.proximity.ProximityObserver;
import com.estimote.proximity_sdk.proximity.ProximityObserverBuilder;
import com.estimote.proximity_sdk.proximity.ProximityZone;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;


public class HomeActivity2 extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private ProximityObserver proximityObserver;
    private ProximityObserver.Handler proximityHandler = null;
    private ImageButton startPlaying;
    private SeekBar seekBar;
    private MediaPlayer mPlayer = null;
    private String mFileName = null;
    private AssetFileDescriptor descriptor;
    public static final String PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);

        audioBuild();
        dostuff();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Toast.makeText(getApplicationContext(),"Back button clicked", Toast.LENGTH_SHORT).show();
                super.onBackPressed();
                SharedPreferences preferences = getSharedPreferences(PREFS_NAME,0);
                SharedPreferences.Editor editor=preferences.edit();
                editor.putString("activityFromNotification","true");
                editor.commit();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME,0);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("activityFromNotification","true");
        editor.commit();
    }

    private void audioBuild(){
        startPlaying = (ImageButton) findViewById(R.id.buttonStartPlay);
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        //Corrigir
        /*mFileName = Environment.getExternalStorageDirectory().getAbsolutePath()//;
        mFileName += "/audiorecordtest.3gp";
*/
        Log.e("audioBuild", "startPlaying-> " + startPlaying);
        /*startPlaying.setOnClickListener(new View.OnClickListener() {
            boolean mStartPlaying = true;
            @Override
            public void onClick(View v) {
                //onPlay(mStartPlaying);
                startAudio();
                if (mStartPlaying) {
                    startPlaying.setImageResource(R.drawable.ic_media_pause);
                } else {
                    startPlaying.setImageResource(R.drawable.ic_media_play);
                }
                mStartPlaying = !mStartPlaying;
            }
        });*/
    }

    public void startAudio(View view) {
        if(mPlayer != null && mPlayer.isPlaying()){
            Toast toastPause = Toast.makeText(getApplicationContext(), "Pause", Toast.LENGTH_SHORT);
            toastPause.show();
            mPlayer.pause();
            ((ImageButton)view).setImageResource((R.drawable.ic_media_play));
            //startPlaying.setImageResource(R.drawable.ic_media_play);

        } else if(mPlayer != null){
            Toast toastPlay = Toast.makeText(getApplicationContext(), "PLay", Toast.LENGTH_SHORT);
            toastPlay.show();
            mPlayer.start();
            ((ImageButton)view).setImageResource(R.drawable.ic_media_pause);
            //startPlaying.setImageResource(R.drawable.ic_media_pause);
        }else{
            Toast toastNew = Toast.makeText(getApplicationContext(), "NEW", Toast.LENGTH_SHORT);
            toastNew.show();
            int resID =getResources().getIdentifier("foca_comum", "raw", getPackageName());
            Log.e("startAudio", "failed -> " + resID);
            mPlayer = MediaPlayer.create(this,resID);
            try {
                //descriptor = getAssets().openFd("foca_comum.ogg");
                //mPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                try {
                    mPlayer.prepare();
                }catch (IllegalStateException e){
                    Log.e("startAudio", e.toString());
                }
                mPlayer.start();
                startPlaying.setImageResource(R.drawable.ic_media_pause);
                ((ImageButton)view).setImageResource(R.drawable.ic_media_pause);

            } catch (IOException e) {
                Log.e("startAudio", "prepare() failed");
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        if(fromUser){
            mPlayer.seekTo(progress);
            seekBar.setProgress(progress);
        }
        else{
            // the event was fired from code and you shouldn't call player.seekTo()
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

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

        // Capture the layout's TextView and set the string as its text
        /*VideoView vidView = findViewById(R.id.videoView);
        vidView.setMediaController(new MediaController(this));
        vidView.setVideoURI(Uri.parse(path+"/"+fileName));
        //vidView.requestFocus();
        vidView.start();

        /*try {
            mp.setDataSource(path+"/"+fileName);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            mp.prepare();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mp.start();*/
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