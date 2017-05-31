package com.ddscanner.events;

import com.google.android.gms.maps.model.LatLngBounds;

public class PlaceChoosedEvent {

    private LatLngBounds latLngBounds;

    public PlaceChoosedEvent(LatLngBounds latLngBounds) {
        this.latLngBounds = latLngBounds;
    }

    public LatLngBounds getLatLngBounds() {
        return this.latLngBounds;
    }

}
