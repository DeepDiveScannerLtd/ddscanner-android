package com.ddscanner.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AchievmentsResponseEntity {

    @SerializedName("completed")
    private List<CompleteAchievement> completeAchievements;

    @SerializedName("pending")
    private List<PendingAchievement> pendingAchievements;

    public List<CompleteAchievement> getCompleteAchievements() {
        return completeAchievements;
    }

    public void setCompleteAchievements(List<CompleteAchievement> completeAchievements) {
        this.completeAchievements = completeAchievements;
    }

    public List<PendingAchievement> getPendingAchievements() {
        return pendingAchievements;
    }

    public void setPendingAchievements(List<PendingAchievement> pendingAchievements) {
        this.pendingAchievements = pendingAchievements;
    }
}
