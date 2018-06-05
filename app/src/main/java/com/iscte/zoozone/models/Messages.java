package com.iscte.zoozone.models;

/**
 * Created by b.coitos on 6/3/2018.
 */

public class Messages {

    private String zoneNotFound;
    private String zoneFound;
    private String notificationMsg;

    public Messages() {}

    public Messages(String zoneNotFound, String zoneFound, String notificationMsg) {
        this.zoneNotFound = zoneNotFound;
        this.zoneFound = zoneFound;
        this.notificationMsg = notificationMsg;
    }

    public String getZoneNotFound() {
        return zoneNotFound;
    }

    public void setZoneNotFound(String zoneNotFound) {
        this.zoneNotFound = zoneNotFound;
    }

    public String getZoneFound() {
        return zoneFound;
    }

    public void setZoneFound(String zoneFound) {
        this.zoneFound = zoneFound;
    }

    public String getNotificationMsg() {
        return notificationMsg;
    }

    public void setNotificationMsg(String notificationMsg) {
        this.notificationMsg = notificationMsg;
    }
}
