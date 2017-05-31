package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DivespotsWrapper {
    @SerializedName("dive_spots")
    private List<DiveSpotShort> divespots = new ArrayList<>();

    public List<DiveSpotShort> getDiveSpots() { return divespots; }

    public void setDiveSpots(List<DiveSpotShort> diveSpotShorts) { this.divespots = divespots; }
}
