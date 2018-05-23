package com.iscte.dam.models;

/**
 * Created by b.coitos on 5/23/2018.
 */

public class Zone {
    public String name;
    public String id;
    public String audioFile;
    public String description;

    public Zone(){}

    public Zone(String name, String id, String audioFile, String description){
        this.name=name;
        this.id=id;
        this.audioFile=audioFile;
        this.description=description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAudioFile() {
        return audioFile;
    }

    public void setAudioFile(String audioFile) {
        this.audioFile = audioFile;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
