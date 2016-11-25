package com.ddscanner.entities;

public class UserComment extends BaseComment {

    private String diveSpotName;
    private String diveSpotImage;
    private String diveSpotId;

    public String getDiveSpotName() {
        return diveSpotName;
    }

    public void setDiveSpotName(String diveSpotName) {
        this.diveSpotName = diveSpotName;
    }

    public String getDiveSpotImage() {
        return diveSpotImage;
    }

    public void setDiveSpotImage(String diveSpotImage) {
        this.diveSpotImage = diveSpotImage;
    }

    public String getDiveSpotId() {
        return diveSpotId;
    }

    public void setDiveSpotId(String diveSpotId) {
        this.diveSpotId = diveSpotId;
    }
}
