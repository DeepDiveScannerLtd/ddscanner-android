package com.ddscanner.entities;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FunDiveDetails extends FunDive implements Serializable {

    @SerializedName("price_by_dives_count")
    private String priceByDivesCount;
    @SerializedName("price_for_optional")
    private String priceForOptional;
    @SerializedName("price_non_diver")
    private String priceNonDiver;
    @SerializedName("whats_included")
    private String whatsIncluded;
    private String schedule;
    @SerializedName("itinerary")
    private String initiary;
    private String requirements;
    @SerializedName("dive_center")
    private DiveCenterProfile diveCenterProfile;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String description;

    public DiveCenterProfile getDiveCenterProfile() {
        return diveCenterProfile;
    }

    public void setDiveCenterProfile(DiveCenterProfile diveCenterProfile) {
        this.diveCenterProfile = diveCenterProfile;
    }

    public String getPriceByDivesCount() {
        return priceByDivesCount;
    }

    public void setPriceByDivesCount(String priceByDivesCount) {
        this.priceByDivesCount = priceByDivesCount;
    }

    public String getPriceForOptional() {
        return priceForOptional;
    }

    public void setPriceForOptional(String priceForOptional) {
        this.priceForOptional = priceForOptional;
    }

    public String getPriceNonDiver() {
        return priceNonDiver;
    }

    public void setPriceNonDiver(String priceNonDiver) {
        this.priceNonDiver = priceNonDiver;
    }

    public String getWhatsIncluded() {
        return whatsIncluded;
    }

    public void setWhatsIncluded(String whatsIncluded) {
        this.whatsIncluded = whatsIncluded;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getInitiary() {
        return initiary;
    }

    public void setInitiary(String initiary) {
        this.initiary = initiary;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }
}
