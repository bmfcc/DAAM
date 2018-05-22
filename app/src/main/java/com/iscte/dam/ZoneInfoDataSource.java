package com.iscte.dam;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.iscte.dam.models.ZoneInfoDB;

/**
 * Created by b.coitos on 5/22/2018.
 */

public class ZoneInfoDataSource {
    protected SQLiteDatabase db;
    protected DatabaseHelper dbHelper;

    public ZoneInfoDataSource(Context c){
        dbHelper = new DatabaseHelper(c);
    }

    //Open DB
    public void open(){
        db = dbHelper.getWritableDatabase();
    }

    //Close Db
    public void close(){
        db.close();
    }

    //Criar nova zona
    public ZoneInfoDB create(String zoneID, String zoneName, String audioFile, String textFile){
        ContentValues values = new ContentValues();
        values.put("zoneID", zoneID);
        values.put("zoneName", zoneName);
        values.put("audioFile", audioFile);
        values.put("audioFile",audioFile);
        values.put("textFile", textFile);

        long lastID = db.insert("zoneInfo", null, values);

        return new ZoneInfoDB(lastID, zoneID,zoneName,audioFile,textFile);
    }

    //Get ZoneInfo
    public ZoneInfoDB get(long id){
        Cursor c = db.rawQuery("SELECT * FROM zoneInfo WHERE id = "+id,null);

        if(c.getCount() == 0){
            return null;
        } else{
            c.moveToFirst();
            ZoneInfoDB zone = new ZoneInfoDB(id,c.getString(1),c.getString(2),c.getString(3), c.getString(4));
            c.close();
            return zone;
        }
    }


}
