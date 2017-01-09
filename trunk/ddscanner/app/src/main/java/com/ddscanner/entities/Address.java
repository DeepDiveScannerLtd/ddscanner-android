package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Address implements Serializable {

    @SerializedName("address")
    private String name;
    private String lat;
    private String lng;

    public Address(String name, String lat, String lng) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }
}