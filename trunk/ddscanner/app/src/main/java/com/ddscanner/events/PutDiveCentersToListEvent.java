package com.ddscanner.events;

import com.ddscanner.entities.DiveCenter;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by lashket on 6.6.16.
 */
public class PutDiveCentersToListEvent implements Serializable{

    private ArrayList<DiveCenter> diveCenters;
    private String path;

    public PutDiveCentersToListEvent(ArrayList<DiveCenter> diveCenters, String path) {
        this.diveCenters = diveCenters;
        this.path = path;
    }

    public ArrayList<DiveCenter> getDiveCenters() {
        return diveCenters;
    }

    public String getPath() {
        return path;
    }
}
