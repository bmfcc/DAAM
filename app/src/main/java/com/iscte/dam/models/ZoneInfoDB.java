package com.iscte.dam.models;

/**
 * Created by b.coitos on 5/22/2018.
 */

public class ZoneInfoDB {
    protected long id;
    protected String zoneID, zoneName, audioFile, textFile;

    public ZoneInfoDB(){}

    public ZoneInfoDB(long id, String zoneID, String zoneName, String audioFile, String textFile){
        this.id = id;
        this.zoneID = zoneID;
        this.zoneName = zoneName;
        this.audioFile = audioFile;
        this.textFile = textFile;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getZoneID() {
        return zoneID;
    }

    public void setZoneID(String zoneID) {
        this.zoneID = zoneID;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getAudioFile() {
        return audioFile;
    }

    public void setAudioFile(String audioFile) {
        this.audioFile = audioFile;
    }

    public String getTextFile() {
        return textFile;
    }

    public void setTextFile(String textFile) {
        this.textFile = textFile;
    }
}
