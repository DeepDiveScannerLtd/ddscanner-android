package com.ddscanner.entities;

import java.util.List;

public class UserResponseEntity {

    private User user;
    private List<ProfileAchievement> achievements;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<ProfileAchievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<ProfileAchievement> achievements) {
        this.achievements = achievements;
    }
}
