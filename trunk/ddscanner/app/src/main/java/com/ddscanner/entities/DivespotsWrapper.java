package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lashket on 21.1.16.
 */
public class DivespotsWrapper {
    @SerializedName("dive_spots")
    private List<DiveSpot> divespots = new ArrayList<>();

    public List<DiveSpot> getDiveSpots() { return divespots; }

    public void setDiveSpots(List<DiveSpot> diveSpots) { this.divespots = divespots; }
}
