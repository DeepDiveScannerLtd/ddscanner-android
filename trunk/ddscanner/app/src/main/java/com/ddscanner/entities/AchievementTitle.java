package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AchievementTitle {

    private String name;
    private String image;
    private String subtitle;
    private String goal;
    @SerializedName("needed_points")
    private int pointsToGoal;
    private ArrayList<AchievementTitleDetails> countries;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public int getPointsToGoal() {
        return pointsToGoal;
    }

    public void setPointsToGoal(int pointsToGoal) {
        this.pointsToGoal = pointsToGoal;
    }

    public ArrayList<AchievementTitleDetails> getCountries() {
        return countries;
    }

    public void setCountries(ArrayList<AchievementTitleDetails> countries) {
        this.countries = countries;
    }
}
