package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

public class SelfCommentEntity extends Comment {

    @SerializedName("dive_spot")
    private DiveSpotShort diveSpot;

    public DiveSpotShort getDiveSpot() {
        return diveSpot;
    }

    public void setDiveSpot(DiveSpotShort diveSpot) {
        this.diveSpot = diveSpot;
    }
}
