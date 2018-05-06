package com.iscte.dam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //getSharedPreferences(PREFS_NAME, 0).edit().clear().commit();
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME,0);
        String language = preferences.getString("selected_language", "Default");

        if(language.equals("Default")){
            selectLanguage();
            finish();
        }else {

            // Get the Intent that started this activity and extract the string
            Intent intent = getIntent();
            String message = intent.getStringExtra(MainActivity.SELECTED_LANGUAGE);

            // Capture the layout's TextView and set the string as its text
            TextView textView = findViewById(R.id.textView);
            textView.setText(message);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void selectedZooLocation(View view){
        int viewID = view.getId();
        String resourceName = getResources().getResourceEntryName(viewID);
        Log.d("MainAtivitityLog",resourceName);
        Intent intent = new Intent(this, HomeActivity2.class);
        startActivity(intent);
    }

    public void selectLanguage(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
