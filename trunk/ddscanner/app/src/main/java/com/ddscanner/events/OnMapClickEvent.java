package com.ddscanner.events;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by lashket on 22.4.16.
 */
public class OnMapClickEvent {

    private Marker marker;

    public OnMapClickEvent(Marker marker) {
        this.marker = marker;
    }

    public Marker getMarker() {
        return this.marker;
    }

}
