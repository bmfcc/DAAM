package com.iscte.dam;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by b.coitos on 5/22/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, "ZOOZone_DATABASE", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE zoneInfo (id INTEGER PRIMARY KEY, zoneID VARCHAR(32), zoneName VARCHAR(64), audioFile VARCHAR(64), textFile VARCHAR(64))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS zoneInfo");
        onCreate(db);
    }
}
