package com.ddscanner.entities;

import java.util.List;

public class AchievmentsResponseEntity {

    private List<CompleteAchievement> completeAchievements;
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
