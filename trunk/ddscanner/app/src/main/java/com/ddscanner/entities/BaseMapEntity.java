package com.ddscanner.entities;


import com.ddscanner.utils.Helpers;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.ArrayList;

public class BaseMapEntity implements ClusterItem {

    private int id;
    private String name;
    private int type;
    private float lat;
    private float lng;
    private float rating;
    private String photo;
    private ArrayList<Address> addresses;
    private ArrayList<String> languages;
    private boolean isDiveCenter;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public ArrayList<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(ArrayList<Address> addresses) {
        this.addresses = addresses;
    }

    public ArrayList<String> getLanguages() {
        return languages;
    }

    public void setLanguages(ArrayList<String> languages) {
        this.languages = languages;
    }

    public boolean isDiveCenter() {
        return isDiveCenter;
    }

    @Override
    public LatLng getPosition() {
        if (addresses != null) {
            isDiveCenter = true;
            return new LatLng(addresses.get(0).getLat(), addresses.get(0).getLng());
        }
        isDiveCenter = false;
        return new LatLng(lat, lng);
    }

    public String getObject() {
        return Helpers.getDiveSpotType(type);
    }

    public String getLanguagesString() {
        StringBuilder output = new StringBuilder();
        if (languages != null) {
            for (int i = 0; i < languages.size(); i++) {
                if (i < languages.size() - 1) {
                    output.append(languages.get(i)).append(", ");
                } else {
                    output.append(languages.get(i));
                }
            }
        }
        return output.toString();
    }

}
