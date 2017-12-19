package com.ddscanner.entities;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class DailyTourDetails extends DailyTour implements Serializable{

    private ArrayList<String> images;
    @SerializedName("whats_included")
    private String whatsIncluded;
    @SerializedName("schedule")
    private String schedule;
    @SerializedName("time_start_and_end")
    private String startEndTime;
    private String description;
    private String photosCount;
    @SerializedName("dive_center")
    private DiveCenterProfile diveCenterProfile;
    @SerializedName("itinerary")
    private String initiary;
    @SerializedName("price_diver")
    private String priceDiver;
    @SerializedName("price_non_diver")
    private String priceNonDiver;
    @SerializedName("price_equipment")
    private String priceEquipment;

    public String getPriceNonDiver() {
        return priceNonDiver;
    }

    public void setPriceNonDiver(String priceNonDiver) {
        this.priceNonDiver = priceNonDiver;
    }

    public String getPriceEquipment() {
        return priceEquipment;
    }

    public void setPriceEquipment(String priceEquipment) {
        this.priceEquipment = priceEquipment;
    }

    public String getPriceDiver() {
        return priceDiver;
    }

    public void setPriceDiver(String priceDiver) {
        this.priceDiver = priceDiver;
    }

    public String getInitiary() {
        return initiary;
    }

    public void setInitiary(String initiary) {
        this.initiary = initiary;
    }

    public DiveCenterProfile getDiveCenterProfile() {
        return diveCenterProfile;
    }

    public void setDiveCenterProfile(DiveCenterProfile diveCenterProfile) {
        this.diveCenterProfile = diveCenterProfile;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
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

    public String getStartEndTime() {
        return startEndTime;
    }

    public void setStartEndTime(String startEndTime) {
        this.startEndTime = startEndTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhotosCount() {
        if (images == null) {
            photosCount = "0";
            return photosCount;
        }
        photosCount = String.valueOf(images.size());
        return photosCount;
    }
}
