package com.ddscanner.events;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by lashket on 22.4.16.
 */
public class OnMapClickEvent {

    private Marker marker;
    private boolean isNew;

    public OnMapClickEvent(Marker marker, boolean isNew) {
        this.isNew = isNew;
        this.marker = marker;
    }

    public Marker getMarker() {
        return this.marker;
    }

    public boolean getIsNew() {
        return this.isNew;
    }

}
