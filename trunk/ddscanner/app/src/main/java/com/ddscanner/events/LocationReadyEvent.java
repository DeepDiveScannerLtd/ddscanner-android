package com.ddscanner.events;

import android.location.Location;

public class LocationReadyEvent {
    private Location location;

    public LocationReadyEvent(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}
