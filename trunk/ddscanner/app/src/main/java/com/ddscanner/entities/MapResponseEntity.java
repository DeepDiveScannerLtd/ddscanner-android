package com.ddscanner.entities;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MapResponseEntity {

    @SerializedName("dive_spots")
    private ArrayList<BaseMapEntity> diveSpots;
    @SerializedName("dive_centers")
    private ArrayList<BaseMapEntity> diveCenters;

    public ArrayList<BaseMapEntity> getDiveSpots() {
        return diveSpots;
    }

    public void setDiveSpots(ArrayList<BaseMapEntity> diveSpots) {
        this.diveSpots = diveSpots;
    }

    public ArrayList<BaseMapEntity> getDiveCenters() {
        return diveCenters;
    }

    public void setDiveCenters(ArrayList<BaseMapEntity> diveCenters) {
        this.diveCenters = diveCenters;
    }

    public ArrayList<BaseMapEntity> getAllEntities() {
        ArrayList<BaseMapEntity> baseMapEntities = new ArrayList<>();
        if (diveSpots != null) {
            baseMapEntities.addAll(diveSpots);
        }
        if (diveCenters != null) {
            baseMapEntities.addAll(diveCenters);
        }
        return baseMapEntities;
    }

}
