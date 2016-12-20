package com.ddscanner.entities;

import java.util.ArrayList;

public class GoogleMapsGeocodeResponseEntity {

    private ArrayList<GeocodingResultEntity> results;

    public ArrayList<GeocodingResultEntity> getResults() {
        return results;
    }

    public void setResults(ArrayList<GeocodingResultEntity> results) {
        this.results = results;
    }
}
