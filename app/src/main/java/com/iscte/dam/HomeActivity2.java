package com.iscte.dam;

/**
 * Created by JD on 09-03-2018.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.time.LocalDate;


public class HomeActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        dostuff();
    }

    public void dostuff(){
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.SELECTED_LANGUAGE);

    }
}