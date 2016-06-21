package com.ddscanner.events;

import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by lashket on 21.6.16.
 */
public class LocationChosedEvent {

    private LatLngBounds latLngBounds;

    public LocationChosedEvent(LatLngBounds latLngBounds) {
        this.latLngBounds = latLngBounds;
    }

    public LatLngBounds getLatLngBounds() {
        return this.latLngBounds;
    }

}
