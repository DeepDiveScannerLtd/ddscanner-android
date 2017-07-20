package com.ddscanner.entities;

import java.util.ArrayList;

public class DiveCentersResponseEntity {
    private ArrayList<DiveCenter> divecenters;
    private  String logoPath;

    public ArrayList<DiveCenter> getDivecenters() {
        return divecenters;
    }

    public void setDivecenters(ArrayList<DiveCenter> divecenters) {
        this.divecenters = divecenters;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }
}
