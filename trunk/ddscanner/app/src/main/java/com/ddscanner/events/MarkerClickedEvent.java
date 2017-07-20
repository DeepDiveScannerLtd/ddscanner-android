package com.ddscanner.events;

import com.google.android.gms.maps.model.Marker;

public class MarkerClickedEvent {

    private Marker marker;

    public MarkerClickedEvent(Marker marker) {
        this.marker = marker;
    }

    public Marker getMarker() {
        return marker;
    }
}
