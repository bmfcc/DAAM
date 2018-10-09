package com.iscte.zoozone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.iscte.zoozone.models.MapInfo;
import com.iscte.zoozone.models.VisitedZones;

import java.io.File;
import java.util.ArrayList;

public class MapActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "MyPrefsFile";

    private ImageView mapImage;

    private ArrayList<MapInfo> mapInfos;

    private DatabaseReference dbSettingsRef;
    private DatabaseReference dbMapInfoRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbSettingsRef = database.getReference("Settings");
        dbMapInfoRef = database.getReference("MapInfo");

        Intent myIntent = getIntent(); // gets the previously created intent
        String title = myIntent.getStringExtra("mapTitle");
        ((TextView)findViewById(R.id.mapTitle)).setText(title);

        mapInfos = new ArrayList<>();

        getVisitedZones();

        getDBInfo();
    }

    private void imageBuild(String mapImageName) {
        mapImage = findViewById(R.id.mapImage);

        //StorageReference imageReference = imagesRef.child("Map/" + settings.getMapImageName());

        File imgFile = new File(this.getFilesDir(), "map/" + mapImageName);//settings.getMapImageName());

        if (imgFile.exists()) {

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            GlideApp.with(this).asBitmap().load(myBitmap).into(mapImage);


            android.graphics.Bitmap.Config bitmapConfig =
                    myBitmap.getConfig();

            if (bitmapConfig == null) {
                bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
            }

            myBitmap = myBitmap.copy(bitmapConfig, true);

            Canvas canvas = new Canvas(myBitmap);

            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(getResources().getColor(R.color.paint_map));

            for(MapInfo mapInfo: mapInfos) {
                canvas.drawCircle(mapInfo.getXx(), mapInfo.getYy(), mapInfo.getRadius(), paint);
            }

            GlideApp.with(MapActivity.this).asBitmap().load(myBitmap).into(mapImage);
        }
    }

    private void getDBInfo(){

        final ValueEventListener postSettingsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                //settings = dataSnapshot.getValue(Settings.class);
                imageBuild(dataSnapshot.child("mapImageName").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("GetBDValueError", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        //dbSettingsRef.addListenerForSingleValueEvent(postSettingsListener);

        ValueEventListener postMapInfoListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                ArrayList<String> vZones = getVisitedZones();

                for(String zone: vZones){
                    MapInfo mapInfo = dataSnapshot.child(zone).getValue(MapInfo.class);

                    if(mapInfo != null)
                        mapInfos.add(mapInfo);
                }

                dbSettingsRef.addListenerForSingleValueEvent(postSettingsListener);
                //imageBuild();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("GetBDValueError", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        dbMapInfoRef.addListenerForSingleValueEvent(postMapInfoListener);
    }

    private ArrayList<String> getVisitedZones(){

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME,0);
        String visitedZones = preferences.getString("visited_zones", "Default");

        if(visitedZones.equals("Default")){

            ArrayList<String> vZones = new ArrayList<>();

            return  vZones;

        }else{
            ArrayList<String> vZones = new VisitedZones(visitedZones).getVisitedZonesArr();

            return vZones;
        }

    }
}
