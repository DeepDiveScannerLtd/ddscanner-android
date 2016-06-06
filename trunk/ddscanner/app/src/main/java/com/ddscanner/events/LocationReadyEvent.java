package com.ddscanner.events;

import android.location.Location;
import android.util.SparseArray;

import com.ddscanner.utils.LogUtils;

import java.util.ArrayList;
import java.util.HashSet;

public class LocationReadyEvent {

    private static final String TAG = LocationReadyEvent.class.getName();
    private Location location;
    private HashSet<Integer> requestCodes = new HashSet<>();

    public LocationReadyEvent(Location location, HashSet<Integer> requestCodes) {
        this.location = location;
        this.requestCodes.addAll(requestCodes);
        LogUtils.i(TAG, "location check: requestCodes = " + requestCodes);

    }

    public Location getLocation() {
        return location;
    }

    public HashSet<Integer> getRequestCodes() {
        return requestCodes;
    }
}
