package com.ddscanner.events;

import com.google.android.gms.maps.model.Marker;

public class InfowWindowOpenedEvent {

    private Marker marker;

    public InfowWindowOpenedEvent(Marker marker) {
        this.marker = marker;
    }

    public Marker getMarker() {
        return this.marker;
    }

}
