package com.iscte.zoozone;

/**
 * Created by JD on 09-03-2018.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iscte.zoozone.models.Zone;

import java.io.IOException;


public class ZoneInfo extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private ImageButton startPlaying;
    private ImageView animalImage;
    private TextView tViewDesc;
    private TextView tViewTitle;
    private SeekBar seekBar;
    private MediaPlayer mPlayer = null;
    public static final String PREFS_NAME = "MyPrefsFile";
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

        if(stringZone.equals("Default")){
            Toast toast = Toast.makeText(getApplicationContext(), "Zone not found!", Toast.LENGTH_SHORT);
            toast.show();
            finish();
            return;
        }

        String language = preferences.getString("selected_language","Default");

        if(language.equals("Default")){
            final AlertDialog.Builder dialBuilder1 = new AlertDialog.Builder(this);
            dialBuilder1.setMessage("Language not choosed! Information displayed will appear in English.")
                    .setTitle("My Zone");
            dialBuilder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    dialog.dismiss();
                }
            });
            language="EN";
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("Zones").child(language+"/"+stringZone);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        imagesRef = storageRef.child("Images/Zones");
        audioRef = storageRef.child("Audios/"+language);

        getDBInfo();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();

                mPlayer.stop();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

        mPlayer.stop();
    }

    private void imageBuild(){
        animalImage = findViewById(R.id.animalImage);

        StorageReference imageReference = imagesRef.child(zone.getImageFile());

        GlideApp.with(this).load(imageReference).into(animalImage);

    }

    private void audioBuild(){
        startPlaying = (ImageButton) findViewById(R.id.buttonStartPlay);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        handler = new Handler();

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

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
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
            Log.w("playCycle","isplaying");
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
            mPlayer.pause();
            ((ImageButton)view).setImageResource((R.drawable.ic_media_play));

        } else if(mPlayer != null){
            mPlayer.start();
            ((ImageButton)view).setImageResource(R.drawable.ic_media_pause);
            playCycle();

        }else{
            try {
                try {
                    mPlayer.prepare();
                }catch (IllegalStateException e){
                    Log.w("app", "ZoneInfo - startAudio - prepare() failed");
                }
                mPlayer.start();
                startPlaying.setImageResource(R.drawable.ic_media_pause);
                ((ImageButton)view).setImageResource(R.drawable.ic_media_pause);

            } catch (IOException e) {
                Log.w("app", "ZoneInfo - startAudio - prepare() failed");
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

        if(mPlayer!=null) {
            mPlayer.release();
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
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