package com.ddscanner.entities;

import java.util.List;

public class UserResponseEntity {

    private UserOld userOld;
    private List<ProfileAchievement> achievements;

    public UserOld getUserOld() {
        return userOld;
    }

    public void setUserOld(UserOld userOld) {
        this.userOld = userOld;
    }

    public List<ProfileAchievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<ProfileAchievement> achievements) {
        this.achievements = achievements;
    }
}
