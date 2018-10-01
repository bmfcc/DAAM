package com.iscte.zoozone.models;


import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by b.coitos on 9/30/2018.
 */

public class VisitedZones {

    private String visitedZones;
    private ArrayList<String> visitedZonesArr;

    public VisitedZones(String visitedZones) {

        this.visitedZones=visitedZones;
        toArray();
    }

    @Override
    public String toString(){
        visitedZones = TextUtils.join(";",visitedZonesArr);
        return visitedZones;
    }

    public void addZone(String zoneID){

        visitedZonesArr.add(zoneID);

    }

    private void toArray(){
        visitedZonesArr = new ArrayList<>(Arrays.asList(visitedZones.split(";")));
    }

    public ArrayList<String> getVisitedZonesArr(){
        return visitedZonesArr;
    }
}
