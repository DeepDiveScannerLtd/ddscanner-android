package com.ddscanner.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lashket on 21.1.16.
 */
public class DivespotsWrapper {
    private List<DiveSpot> divespots = new ArrayList<>();

    public List<DiveSpot> getDiveSpots() { return divespots; }

    public void setDiveSpots(List<DiveSpot> diveSpots) { this.divespots = divespots; }
}
