package com.ddscanner.events;

import com.ddscanner.entities.DiveCenter;

public class DiveCenterMarkerClickEvent {

    private DiveCenter diveCenter;

    public DiveCenterMarkerClickEvent(DiveCenter diveCenter) {
        this.diveCenter = diveCenter;
    }

    public DiveCenter getDiveCenter() {
        return diveCenter;
    }

}
