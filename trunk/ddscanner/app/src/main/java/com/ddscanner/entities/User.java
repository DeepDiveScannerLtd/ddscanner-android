package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {

    private String name;
    private String photo;
    private ProfileCounters counters;
    private List<ProfileAchievement> achievements;
    private List<String> photos;
    private int type;
    private String token;
    @SerializedName("diving_skill")
    private Integer diverLevel;
    @SerializedName("about")
    private String about;

    public Integer getDiverLevel() {
        return diverLevel;
    }

    public void setDiverLevel(Integer diverLevel) {
        this.diverLevel = diverLevel;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

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
