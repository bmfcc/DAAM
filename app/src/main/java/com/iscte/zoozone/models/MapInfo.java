package com.iscte.zoozone.models;

/**
 * Created by b.coitos on 9/30/2018.
 */

public class MapInfo {

    private long xx;
    private long yy;
    private long radius;

    public MapInfo(long xx, long yy, long radius) {
        this.xx = xx;
        this.yy = yy;
        this.radius = radius;
    }

    public MapInfo() {
    }

    public long getXx() {
        return xx;
    }

    public void setXx(long xx) {
        this.xx = xx;
    }

    public long getYy() {
        return yy;
    }

    public void setYy(long yy) {
        this.yy = yy;
    }

    public long getRadius() {
        return radius;
    }

    public void setRadius(long radius) {
        this.radius = radius;
    }
}
