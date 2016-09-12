package com.ddscanner.events;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by lashket on 14.6.16.
 */
public class InfowWindowOpenedEvent {

    private Marker marker;

    public InfowWindowOpenedEvent(Marker marker) {
        this.marker = marker;
    }

    public Marker getMarker() {
        return this.marker;
    }

}
