package com.ddscanner.events;

import com.google.android.gms.maps.model.LatLng;

public class NewDiveSpotAddedEvent {

    private LatLng latLng;
    private String diveSpotId;

    public NewDiveSpotAddedEvent(LatLng latLng, String diveSpotId) {
        this.latLng = latLng;
        this.diveSpotId = diveSpotId;
    }

    public LatLng getLatLng() {
        return this.latLng;
    }

    public String getDiveSpotId() {
        return this.diveSpotId;
    }

}
