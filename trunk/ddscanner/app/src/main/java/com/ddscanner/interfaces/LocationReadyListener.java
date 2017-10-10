package com.ddscanner.interfaces;


import com.mapbox.mapboxsdk.geometry.LatLng;

public interface LocationReadyListener {
    void onLocationGetted(LatLng latLng);
}
