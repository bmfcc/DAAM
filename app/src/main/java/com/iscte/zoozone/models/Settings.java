package com.iscte.zoozone.models;

/**
 * Created by b.coitos on 9/27/2018.
 */

public class Settings {

    private String mapImageName;

    public Settings(String mapImageName) {
        this.mapImageName = mapImageName;
    }

    public Settings() {
    }

    public String getMapImageName() {
        return mapImageName;
    }

    public void setMapImageName(String mapImageName) {
        this.mapImageName = mapImageName;
    }
}
