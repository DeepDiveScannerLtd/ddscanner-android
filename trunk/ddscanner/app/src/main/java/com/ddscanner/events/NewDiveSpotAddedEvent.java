package com.ddscanner.events;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Lenovo on 07.09.2016.
 */
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
