package com.ddscanner.entities;


import com.google.gson.annotations.SerializedName;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;

public class SearchFeature {

    @SerializedName("place_name")
    private String placeName;
    @SerializedName("bbb")
    private double[] coordiates;

    public double[] getCoordiates() {
        return coordiates;
    }

    public void setCoordiates(double[] coordiates) {
        this.coordiates = coordiates;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public LatLngBounds getBounds() {
        LatLngBounds latLngBounds = LatLngBounds.from(coordiates[4], coordiates[3], coordiates[2], coordiates[1]);
        return latLngBounds;
    }

}
