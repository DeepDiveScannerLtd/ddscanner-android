package com.ddscanner.entities;


import com.google.gson.annotations.SerializedName;

public class SearchFeature {

    @SerializedName("place_name")
    private String placeName;

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }
}
