package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

public class DiveSpotResponseEntity {

    @SerializedName("dive_spot")
    private DiveSpotDetailsEntity diveSpot;

    public DiveSpotDetailsEntity getDiveSpot() {
        return diveSpot;
    }

    public void setDiveSpot(DiveSpotDetailsEntity diveSpot) {
        this.diveSpot = diveSpot;
    }
}
