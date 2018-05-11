package com.iscte.dam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class SelectLanguage extends AppCompatActivity {

    public static final String SELECTED_LANGUAGE = "com.iscte.dam.SelectLanguage.LANGUAGE";
    public static final String PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_language);
    }

    public void selectLanguage(View view) {
        int viewID = view.getId();
        String resourceName = getResources().getResourceEntryName(viewID);
        Log.d("MainAtivitityLog",resourceName);

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME,0);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("selected_language",resourceName);
        editor.commit();


        Intent intent = new Intent(this, Main2Activity.class);
        //EditText editText = (EditText) findViewById(R.id.editText);
        //String message = editText.getText().toString();
        intent.putExtra(SELECTED_LANGUAGE, resourceName);
        startActivity(intent);
    }
}
