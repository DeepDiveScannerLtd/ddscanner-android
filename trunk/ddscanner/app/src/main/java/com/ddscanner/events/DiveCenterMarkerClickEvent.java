package com.ddscanner.events;

import com.ddscanner.entities.DiveCenter;

public class DiveCenterMarkerClickEvent {

    private DiveCenter diveCenter;
    private String path;

    public DiveCenterMarkerClickEvent(DiveCenter diveCenter, String path) {
        this.path = path;
        this.diveCenter = diveCenter;
    }

    public DiveCenter getDiveCenter() {
        return diveCenter;
    }

    public String getPath() {
        return path;
    }

}
