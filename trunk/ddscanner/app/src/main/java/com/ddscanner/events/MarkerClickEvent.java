package com.ddscanner.events;

import com.ddscanner.entities.DiveSpotShort;

/**
 * Created by lashket on 21.4.16.
 */
public class MarkerClickEvent {

    private DiveSpotShort diveSpotShort;

    public MarkerClickEvent(DiveSpotShort diveSpotShort) {
        this.diveSpotShort = diveSpotShort;
    }

    public DiveSpotShort getDiveSpotShort() {
        return this.diveSpotShort;
    }

}
