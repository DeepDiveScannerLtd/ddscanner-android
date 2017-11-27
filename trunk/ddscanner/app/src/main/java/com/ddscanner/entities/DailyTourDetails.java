package com.ddscanner.entities;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DailyTourDetails extends DailyTour {

    private ArrayList<String> images;
    @SerializedName("number_of_dives")
    private String numberOfDives;
    @SerializedName("whats_included")
    private String whatsIncluded;
    @SerializedName("schedule")
    private String schedule;
    @SerializedName("time_start_and_end")
    private String startEndTime;
    private String description;

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public String getNumberOfDives() {
        return numberOfDives;
    }

    public void setNumberOfDives(String numberOfDives) {
        this.numberOfDives = numberOfDives;
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
}
