package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

public class ProfileResponseEntity {

    private int type;
    @SerializedName("diver_or_instructor")
    private User diver;
    @SerializedName("dive_center")
    private DiveCenterProfile diveCenter;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public User getDiver() {
        return diver;
    }

    public void setDiver(User diver) {
        this.diver = diver;
    }

    public DiveCenterProfile getDiveCenter() {
        return diveCenter;
    }

    public void setDiveCenter(DiveCenterProfile diveCenter) {
        this.diveCenter = diveCenter;
    }
}
