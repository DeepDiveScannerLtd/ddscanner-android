package com.ddscanner.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lashket on 5.2.16.
 */
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
