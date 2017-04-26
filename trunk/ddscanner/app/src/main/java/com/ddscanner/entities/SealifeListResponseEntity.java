package com.ddscanner.entities;

import java.util.ArrayList;

public class SealifeListResponseEntity {

    private ArrayList<SealifeShort> all;
    private ArrayList<SealifeShort> nearby;


    public ArrayList<SealifeShort> getAll() {
        return all;
    }

    public void setAll(ArrayList<SealifeShort> all) {
        this.all = all;
    }

    public ArrayList<SealifeShort> getNearby() {
        return nearby;
    }

    public void setNearby(ArrayList<SealifeShort> nearby) {
        this.nearby = nearby;
    }
}
