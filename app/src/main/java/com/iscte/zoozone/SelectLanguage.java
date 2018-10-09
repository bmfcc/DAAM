package com.iscte.zoozone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iscte.zoozone.models.Language;
import com.iscte.zoozone.models.SelectLanguageInfo;

import java.io.File;
import java.util.ArrayList;

public class SelectLanguage extends AppCompatActivity {

    public static final String PREFS_NAME = "MyPrefsFile";

    private ArrayList<Language> languagesList;
    private String language;

    private DatabaseReference dbLanguagesRef;
    private DatabaseReference dbSettingsRef;
    private DatabaseReference dbSelectLanguageRef;

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference imagesRef;

    private Spinner spinner;
    private Button confirmButton;

    private ConstraintLayout constLayoutProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_language);

        constLayoutProgress = findViewById(R.id.constLayoutProgress);
        constLayoutProgress.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // ignore all touch events
                return true;
            }
        });

        constLayoutProgress.setVisibility(View.VISIBLE);

        spinner = findViewById(R.id.spinner);
        //spinner.setClickable(false);

        confirmButton = findViewById(R.id.OK_button);
        //confirmButton.setClickable(false);

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME,0);
        language = preferences.getString("selected_language", "Default");

        getDBInfo();

    }

    public void selectLanguage(View view) {

        spinner = findViewById(R.id.spinner);
        Language ln = (Language)spinner.getSelectedItem();

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME,0);
        String previousLanguage = preferences.getString("selected_language", "Default");

        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("selected_language",ln.getInitials());
        editor.commit();

        Intent intent = new Intent(this, Main2Activity.class);
        intent.putExtra("prevLanguage",previousLanguage);
        startActivity(intent);
        finish();
    }

    private void getDBInfo(){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbSettingsRef = database.getReference("Settings");

        if(language.equals("Default")){
            dbLanguagesRef = database.getReference("Languages").child("EN");
            dbSelectLanguageRef = database.getReference("SelectLanguageInfo").child("EN");
        }
        else {
            dbLanguagesRef = database.getReference("Languages").child(language);
            dbSelectLanguageRef = database.getReference("SelectLanguageInfo").child(language);
        }

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                GenericTypeIndicator<ArrayList<Language>> arrayLang = new GenericTypeIndicator<ArrayList<Language>>() {};

                languagesList = dataSnapshot.getValue(arrayLang);

                ArrayAdapter<Language> adapter = new ArrayAdapter<Language>(SelectLanguage.this,R.layout.spinner_item, languagesList);
                adapter.setDropDownViewResource(R.layout.spinner_item);
                spinner.setAdapter(adapter);

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("GetBDValueError", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        dbLanguagesRef.addListenerForSingleValueEvent(postListener);

        ValueEventListener postSettingsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                getStorageInfo(dataSnapshot.child("mapImageName").getValue().toString(),dataSnapshot.child("logoImage").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("GetBDValueError", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        dbSettingsRef.addListenerForSingleValueEvent(postSettingsListener);

        ValueEventListener postInfoListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                SelectLanguageInfo selectLanguageInfo = dataSnapshot.getValue(SelectLanguageInfo.class);

                ((TextView)findViewById(R.id.languageTextView)).setText(selectLanguageInfo.getSelectLanguageMsg());
                confirmButton.setText(selectLanguageInfo.getConfirmButton());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("GetBDValueError", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        dbSelectLanguageRef.addListenerForSingleValueEvent(postInfoListener);

    }

    private void getStorageInfo(String mapImageName, String logoImage){

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        imagesRef = storageRef.child("Images");

        //Download MapImage

        File file = new File(this.getFilesDir(), "map");

        if(file.exists()){
            File mapImageFile = new File(file,mapImageName);

            if(isNewVersion()){


            }
        } else{
            file.mkdirs();
            File map = new File(file,mapImageName);

            imagesRef.child("Map/" + mapImageName).getFile(map).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception exception) {
                    // Handle any errors
                }
            });
        }

        //Set Logo Image
        GlideApp.with(this).load(imagesRef.child("Logo/"+logoImage)).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                constLayoutProgress.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
                constLayoutProgress.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                constLayoutProgress.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
                constLayoutProgress.setVisibility(View.GONE);
                return false;
            }
        }).into((ImageView)findViewById(R.id.logoImage));

    }


    private boolean isNewVersion(){
        return false;
    }
}
