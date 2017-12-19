package com.ddscanner.entities;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CourseDetails implements Serializable {

    private long id;
    private Certificate certificate;
    private String description;
    private String duration;
    private String inclusions;
    @SerializedName("itinerary")
    private String initiary;
    private Integer divesCount;
    private String price;
    private String fee;
    private DiveCenterProfile diveCenterProfile;

    public DiveCenterProfile getDiveCenterProfile() {
        return diveCenterProfile;
    }

    public void setDiveCenterProfile(DiveCenterProfile diveCenterProfile) {
        this.diveCenterProfile = diveCenterProfile;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getInclusions() {
        return inclusions;
    }

    public void setInclusions(String inclusions) {
        this.inclusions = inclusions;
    }

    public String getInitiary() {
        return initiary;
    }

    public void setInitiary(String initiary) {
        this.initiary = initiary;
    }

    public Integer getDivesCount() {
        return divesCount;
    }

    public void setDivesCount(Integer divesCount) {
        this.divesCount = divesCount;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getDurationDivesString() {
        String returnedString = "";
        if (duration == null) {
            if (divesCount == null) {
                return returnedString;
            }
            returnedString = String.format("%d dives", divesCount);
            return returnedString;
        }
        if (divesCount == null) {
            return duration;
        }
        returnedString = String.format("%s, %d dives", duration, divesCount);
        return returnedString;
    }

//    public Integer getResourceId() {
//        Integer resourceId;
//        if (certificate == null) {
//            return null;
//        }
//
//    }

}
