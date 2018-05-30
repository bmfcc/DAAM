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
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.Requirement;
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory;
import com.estimote.proximity_sdk.proximity.EstimoteCloudCredentials;
import com.estimote.proximity_sdk.proximity.ProximityAttachment;
import com.estimote.proximity_sdk.proximity.ProximityObserver;
import com.estimote.proximity_sdk.proximity.ProximityObserverBuilder;
import com.estimote.proximity_sdk.proximity.ProximityZone;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iscte.dam.models.Zone;

import java.io.IOException;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;


public class ZoneInfo extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private ProximityObserver proximityObserver;
    private ProximityObserver.Handler proximityHandler = null;
    private ImageButton startPlaying;
    private ImageView animalImage;
    private TextView tViewDesc;
    private TextView tViewTitle;
    private SeekBar seekBar;
    private MediaPlayer mPlayer = null;
    private String mFileName = null;
    private AssetFileDescriptor descriptor;
    public static final String PREFS_NAME = "MyPrefsFile";
    private int resID;
    private Handler handler;
    private Runnable runnable;

    private Zone zone = null;

    private DatabaseReference dbRef;

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference imagesRef;
    private StorageReference audioRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zone_info);

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME,0);

        String stringZone = preferences.getString("zoo_location", "Default");

        stringZone = "foca_comum";

        if(stringZone == "Default"){
            Toast toast = Toast.makeText(getApplicationContext(), "Zone not found!", Toast.LENGTH_SHORT);
            toast.show();
            finish();
            return;
        }

        String language = preferences.getString("selected_language","Default");

        if(language == "Default"){
            final AlertDialog.Builder dialBuilder1 = new AlertDialog.Builder(this);
            dialBuilder1.setMessage("Language not choosed! Information displayed will appear in English.")
                    .setTitle("My Zone");
            dialBuilder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    Toast toast = Toast.makeText(getApplicationContext(), "Let's know more", Toast.LENGTH_SHORT);
                    toast.show();
                    dialog.dismiss();
                }
            });
            language="EN";
        }


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("Zones").child(language+"/"+stringZone);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        imagesRef = storageRef.child("Images");
        audioRef = storageRef.child("Audios/"+language);

        getDBInfo();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Toast.makeText(getApplicationContext(),"Back button clicked", Toast.LENGTH_SHORT).show();
                super.onBackPressed();

                //SharedPreferences preferences = getSharedPreferences(PREFS_NAME,0);
                //SharedPreferences.Editor editor=preferences.edit();

                //editor.putString("setupBeacons","false");
                //editor.commit();
                mPlayer.stop();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        //SharedPreferences preferences = getSharedPreferences(PREFS_NAME,0);
        //SharedPreferences.Editor editor=preferences.edit();

        //editor.putString("setupBeacons","false");
        //editor.commit();

        mPlayer.stop();
    }

    private void imageBuild(){
        animalImage = findViewById(R.id.animalImage);

        StorageReference imageReference = imagesRef.child(zone.getImageFile());

        Log.w("TESTSTORAGE",imageReference.getPath());

        GlideApp.with(this).load(imageReference).into(animalImage);

    }

    private void audioBuild(){
        startPlaying = (ImageButton) findViewById(R.id.buttonStartPlay);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        handler = new Handler();

        //resID =getResources().getIdentifier("foca_comum", "raw", getPackageName());
        //mPlayer = MediaPlayer.create(this,resID);

        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        StorageReference audioReference = audioRef.child(zone.getAudioFile());

        audioReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    // Download url of file
                    String url = uri.toString();
                    mPlayer.setDataSource(url);
                    mPlayer.prepare();
                    seekBar.setMax(mPlayer.getDuration());

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        /*mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mPlayer.setDataSource(audioReference.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        /*try {
            mPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //seekBar.setMax(mPlayer.getDuration());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    Toast toastPause = Toast.makeText(getApplicationContext(), "from user", Toast.LENGTH_SHORT);
                    toastPause.show();
                    mPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                startPlaying.setImageResource((R.drawable.ic_media_play));
                mPlayer.seekTo(0);
                seekBar.setProgress(0);
            }
        });
    }

    public void playCycle(){
        seekBar.setProgress(mPlayer.getCurrentPosition());

        if(mPlayer.isPlaying()){
            Log.e("playCycle","isplaying");
            runnable = new Runnable() {
                @Override
                public void run() {
                    playCycle();
                }
            };
            handler.postDelayed(runnable,1000);
        }
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
            playCycle();

        }else{
            Toast toastNew = Toast.makeText(getApplicationContext(), "NEW", Toast.LENGTH_SHORT);
            toastNew.show();

            Log.e("startAudio", "failed -> " + resID);

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

        if(mPlayer!=null) {
            mPlayer.release();
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();

        Toast toast = Toast.makeText(getApplicationContext(), "2nd Activity Resuming", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void textBuild(){

        tViewTitle = (TextView) findViewById(R.id.textViewTitle);

        tViewTitle.setText(zone.getName());

        tViewDesc = (TextView) findViewById(R.id.textViewDesc);

        tViewDesc.setText(zone.getDescription());
        tViewDesc.setMovementMethod(new ScrollingMovementMethod());

    }

    private void getDBInfo(){

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                zone = dataSnapshot.getValue(Zone.class);

                imageBuild();
                audioBuild();
                textBuild();
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
    }

}