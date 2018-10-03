package com.iscte.zoozone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_language);

        spinner = (Spinner) findViewById(R.id.spinner);

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME,0);
        language = preferences.getString("selected_language", "Default");

        getDBInfo();

    }

    public void selectLanguage(View view) {

        spinner = findViewById(R.id.spinner);
        Language ln = (Language)spinner.getSelectedItem();

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME,0);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("selected_language",ln.getInitials());
        editor.commit();

        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
        finish();
    }

    private void getDBInfo(){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbSettingsRef = database.getReference("Settings");

        Log.d("SelectLanguage_getDBInf", "lingua: " + language);

        if(language.equals("Default")){
            dbLanguagesRef = database.getReference("Languages").child("EN");
            dbSelectLanguageRef = database.getReference("SelectLanguageInfo").child("EN");
        }
        else {
            dbLanguagesRef = database.getReference("Languages").child(language);
            dbSelectLanguageRef = database.getReference("SelectLanguageInfo").child(language);
        }

        Log.d("SelectLanguage_getDBInf", "dblangref: OK");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                GenericTypeIndicator<ArrayList<Language>> arrayLang = new GenericTypeIndicator<ArrayList<Language>>() {};

                languagesList = dataSnapshot.getValue(arrayLang);

                Log.d("SelectLanguage_getDBInf", "lingua: " + languagesList);

                ArrayAdapter<Language> adapter = new ArrayAdapter<Language>(SelectLanguage.this,R.layout.spinner_item, languagesList);
                //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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

                //settings = dataSnapshot.getValue(Settings.class);
                //Log.d("SelectLanguage_getDBInf", "settings: "+ settings.getMapImageName());

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
                ((Button)findViewById(R.id.OK_button)).setText(selectLanguageInfo.getConfirmButton());

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

        Log.d("SelectLanguage_getSTInf", "OK ");

        //Download MapImage

        File file = new File(this.getFilesDir(), "map");
        Log.d("SelectLanguage_getSTInf", "getStorageInfo: " + file.getAbsolutePath());
        if(file.exists()){
            File mapImageFile = new File(file,mapImageName);

            Log.d("SelectLanguage_getSTInf", "mapImageFile: " + mapImageFile.getName());
            if(isNewVersion()){


            }
        } else{
            file.mkdirs();
            File map = new File(file,mapImageName);

            Log.d("SelectLanguage_getSTInf", "mapFile: " + map.getName());

            imagesRef.child("Map/" + mapImageName).getFile(map).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    Log.d("SelectLanguage_getSTInf", "onSuccess: GOT IT");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception exception) {
                    // Handle any errors
                }
            });
        }


        //Set Logo Image
        GlideApp.with(this).load(imagesRef.child("Logo/"+logoImage)).into((ImageView)findViewById(R.id.logoImage));

    }


    private boolean isNewVersion(){
        return false;
    }
}
