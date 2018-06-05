package com.iscte.zoozone;

/**
 * Created by b.coitos on 5/26/2018.
 */

public class SetupBeacons {
    private static final SetupBeacons ourInstance = new SetupBeacons();

    public static SetupBeacons getInstance() {
        return ourInstance;
    }

    private String setupBeacons = "False";

    private SetupBeacons() {
        setupBeacons="True";
    }

    public String getSetupBeacons() {
        return setupBeacons;
    }

    public void setSetupBeacons(String setupBeacons) {
        this.setupBeacons = setupBeacons;
    }
}
