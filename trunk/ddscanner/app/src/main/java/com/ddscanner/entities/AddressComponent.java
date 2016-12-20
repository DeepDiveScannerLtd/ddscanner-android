package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

public class AddressComponent {

    @SerializedName("short_name")
    private String shortName;

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}
