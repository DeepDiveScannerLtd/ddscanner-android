package com.ddscanner.entities;

import java.util.List;

public class User {

    private String name;
    private String photo;
    private ProfileCounters counters;
    private List<ProfileAchievement> achievements;

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
