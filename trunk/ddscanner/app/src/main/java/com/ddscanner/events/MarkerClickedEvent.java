package com.ddscanner.events;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MarkerClickedEvent {

    private LatLng position;

    public MarkerClickedEvent(LatLng position) {
        this.position = position;
    }

    public LatLng getPosition() {
        return position;
    }
}
