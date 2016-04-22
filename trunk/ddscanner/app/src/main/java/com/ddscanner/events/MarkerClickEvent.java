package com.ddscanner.events;

import com.ddscanner.entities.DiveSpot;

/**
 * Created by lashket on 21.4.16.
 */
public class MarkerClickEvent {

    private DiveSpot diveSpot;

    public MarkerClickEvent(DiveSpot diveSpot) {
        this.diveSpot = diveSpot;
    }

    public DiveSpot getDiveSpot() {
        return this.diveSpot;
    }

}
