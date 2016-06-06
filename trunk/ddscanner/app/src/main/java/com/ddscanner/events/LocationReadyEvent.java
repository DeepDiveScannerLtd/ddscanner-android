package com.ddscanner.events;

import android.location.Location;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashSet;

public class LocationReadyEvent {
    private Location location;
    private HashSet<Integer> requestCodes;

    public LocationReadyEvent(Location location, HashSet<Integer> requestCodes) {
        this.location = location;
        this.requestCodes = requestCodes;
    }

    public Location getLocation() {
        return location;
    }

    public HashSet<Integer> getRequestCodes() {
        return requestCodes;
    }
}
