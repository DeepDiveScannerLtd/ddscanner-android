package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class User {

    private String name;
    private String photo;
    private ProfileCounters counters;
    private List<ProfileAchievement> achievements;
    private List<String> photos;

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    @SerializedName("about_me")
    private String about;

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public ProfileCounters getCounters() {
        return counters;
    }

    public void setCounters(ProfileCounters counters) {
        this.counters = counters;
    }

    public List<ProfileAchievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<ProfileAchievement> achievements) {
        this.achievements = achievements;
    }
}
