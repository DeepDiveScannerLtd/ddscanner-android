package com.ddscanner.events;

import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by lashket on 21.6.16.
 */
public class LocationChosedEvent {

    private String id;

    public LocationChosedEvent(String id) {
        this.id = id;
    }

    public String getLatLngBounds() {
        return this.id;
    }

}
